package dials;

import akka.actor.Props;
import akka.actor.UntypedActor;
import dials.datastore.DataStore;
import dials.filter.DynamicDataFilter;
import dials.filter.FeatureFilterDataBean;
import dials.filter.FilterData;
import dials.messages.AbandonMessage;
import dials.messages.FilterRetrievalRequestMessage;
import dials.messages.FilterRetrievalResultMessage;

import java.util.Map;

public class FilterRetriever extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof FilterRetrievalRequestMessage) {
            FilterRetrievalRequestMessage requestMessage = (FilterRetrievalRequestMessage) message;

            DataStore dataStore = Dials.getRegisteredDataStore();

            if (!dataStore.isFeatureEnabled(requestMessage.getFeatureName())) {
                requestMessage.getExecutionContext().addExecutionStep("Feature Is Disabled Or Does Not Exist - Abandoning");
                sender().tell(new AbandonMessage(requestMessage), self());
                return;
            }

            FeatureFilterDataBean dataBean = dataStore.getFiltersForFeature(requestMessage.getFeatureName());

            if (dataBean.getFilters() == null || dataBean.getFilters().isEmpty()) {
                requestMessage.getExecutionContext().addExecutionStep("No Filters Detected");
                sender().tell(new FilterRetrievalResultMessage(requestMessage), self());
                return;
            }

            sender().tell(buildResultMessage(requestMessage, dataBean), self());
        }
    }

    private FilterRetrievalResultMessage buildResultMessage(FilterRetrievalRequestMessage message, FeatureFilterDataBean dataBean) {
        FilterRetrievalResultMessage resultMessage = new FilterRetrievalResultMessage(message);

        for (Map.Entry<String, Map<String, Object>> filter : dataBean.getFilters().entrySet()) {
            FilterData data = new FilterData();
            for (Map.Entry<String, Object> filterData : filter.getValue().entrySet()) {
                data.addDataObject(filterData.getKey(), filterData.getValue());
            }

            Class filterClass = getClassForFilter(filter.getKey());

            if (filterClass != null) {
                resultMessage.getExecutionContext().addExecutionStep("Detected Filter - " + filterClass.getSimpleName());

                if (DynamicDataFilter.class.isAssignableFrom(filterClass)) {
                    resultMessage.addFilter(context().actorOf(Props.create(
                            getClassForFilter(filter.getKey()), data, message.getDynamicData(), resultMessage)));
                } else {
                    resultMessage.addFilter(context().actorOf(Props.create(getClassForFilter(filter.getKey()), data, resultMessage)));
                }
            } else {
                resultMessage.getExecutionContext().addExecutionStep("Detected Unknown Filter - " + filter.getKey());
            }
        }
        return resultMessage;
    }

    private Class getClassForFilter(String filterName) {
        return Dials.getAvailableFeatureFilters().get(filterName.toLowerCase());
    }

}


