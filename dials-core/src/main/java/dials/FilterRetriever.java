package dials;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import dials.datastore.DataStore;
import dials.dial.Dialable;
import dials.filter.DynamicDataFilter;
import dials.filter.FeatureFilterDataBean;
import dials.filter.FilterData;
import dials.filter.StaticDataFilter;
import dials.messages.*;

import java.util.Map;

public class FilterRetriever extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof FilterRetrievalRequestMessage) {
            handleFilterRetrievalRequestMessage((FilterRetrievalRequestMessage) message);
        }
    }

    private void handleFilterRetrievalRequestMessage(FilterRetrievalRequestMessage message) {

        DataStore dataStore = message.getConfiguration().getDataStore();

        if (!dataStore.isFeatureEnabled(message.getExecutionContext().getFeatureName())) {
            message.getExecutionContext().addExecutionStep("Feature Is Disabled Or Does Not Exist - Abandoning");
            sender().tell(new AbandonMessage(message), self());
            return;
        }

        FeatureFilterDataBean dataBean = dataStore.getFiltersForFeature(message.getExecutionContext().getFeatureName());

        if (dataBean.getFilters() == null || dataBean.getFilters().isEmpty()) {
            message.getExecutionContext().addExecutionStep("No Filters Detected");
            sender().tell(new FilterRetrievalResultMessage(message), self());
            return;
        }

        sender().tell(buildResultMessage(message, dataBean), self());
    }

    private FilterRetrievalResultMessage buildResultMessage(FilterRetrievalRequestMessage message, FeatureFilterDataBean dataBean) {
        FilterRetrievalResultMessage resultMessage = new FilterRetrievalResultMessage(message);

        for (Map.Entry<String, Map<String, Object>> filter : dataBean.getFilters().entrySet()) {
            FilterData data = new FilterData();
            for (Map.Entry<String, Object> filterData : filter.getValue().entrySet()) {
                data.addDataObject(filterData.getKey(), filterData.getValue());
            }

            Class filterClass = getClassForFilter(resultMessage, filter.getKey());

            if (filterClass != null) {
                resultMessage.getExecutionContext().addExecutionStep("Detected Filter - " + filterClass.getSimpleName());

                ActorRef filterActor = context().actorOf(Props.create(filterClass));

                if (StaticDataFilter.class.isAssignableFrom(filterClass)) {
                    filterActor.tell(new StaticDataFilterApplicationMessage(data, resultMessage), self());
                }

                if (DynamicDataFilter.class.isAssignableFrom(filterClass)) {
                    filterActor.tell(new DynamicDataFilterApplicationMessage(message.getDynamicData(), resultMessage), self());
                }

                if (Dialable.class.isAssignableFrom(filterClass)) {
                    filterActor.tell(new DialableFilterApplicationMessage(message), self());
                }

                resultMessage.addFilter(filterActor);
            } else {
                resultMessage.getExecutionContext().addExecutionStep("Detected Unknown Filter - " + filter.getKey());
            }
        }
        return resultMessage;
    }

    private Class getClassForFilter(ContextualMessage message, String filterName) {
        return message.getConfiguration().getAvailableFeatureFilters().get(filterName.toLowerCase());
    }

}


