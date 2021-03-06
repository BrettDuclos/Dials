package dials;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import dials.dial.Dialable;
import dials.filter.DynamicDataFilter;
import dials.filter.FilterData;
import dials.filter.StaticDataFilter;
import dials.messages.*;
import dials.model.FeatureModel;
import dials.model.FilterModel;
import dials.model.FilterStaticDataModel;

import java.util.Set;

public class FilterRetriever extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ContextualMessage && !((ContextualMessage) message).isAbandoned()) {
            if (message instanceof FilterRetrievalRequestMessage) {
                handleFilterRetrievalRequestMessage((FilterRetrievalRequestMessage) message);
            }
        }
    }

    private void handleFilterRetrievalRequestMessage(FilterRetrievalRequestMessage message) {
        sender().tell(buildResultMessage(message), self());
    }

    private FilterRetrievalResultMessage buildResultMessage(FilterRetrievalRequestMessage message) {
        FeatureModel feature = message.getConfiguration().getRepository()
                .getFeature(message.getExecutionContext().getFeatureName());

        Set<FilterModel> filters = feature.getFilters();

        FilterRetrievalResultMessage resultMessage = new FilterRetrievalResultMessage(message);

        for (FilterModel filter : filters) {
            FilterData staticData = new FilterData();

            for (FilterStaticDataModel staticDataModel : filter.getStaticData()) {
                staticData.addDataObject(staticDataModel.getDataKey(), staticDataModel.getDataValue());
            }

            Class filterClass = getClassForFilter(resultMessage, filter.getFilterName());

            if (filterClass != null) {
                resultMessage.getExecutionContext().addExecutionStep("Detected Filter - " + filterClass.getSimpleName());

                configureFilter(message, resultMessage, filter, staticData, filterClass);
            } else {
                resultMessage.getExecutionContext().addExecutionStep("Detected Unknown Filter - " + filter.getFilterName());
            }
        }
        return resultMessage;
    }

    private void configureFilter(FilterRetrievalRequestMessage message, FilterRetrievalResultMessage resultMessage, FilterModel filter,
                                 FilterData staticData, Class filterClass) {
        ActorRef filterActor = context().actorOf(Props.create(filterClass));

        if (StaticDataFilter.class.isAssignableFrom(filterClass)) {
            filterActor.tell(new StaticDataFilterApplicationMessage(staticData, resultMessage), self());
        }

        if (DynamicDataFilter.class.isAssignableFrom(filterClass)) {
            filterActor.tell(new DynamicDataFilterApplicationMessage(message.getDynamicData(), resultMessage), self());
        }

        if (Dialable.class.isAssignableFrom(filterClass)) {
            if (filter.getDial() != null) {
                filterActor.tell(new DialableFilterApplicationMessage(message, filter.getFilterName()), self());
            }
        }

        resultMessage.addFilter(filterActor);
    }

    private Class getClassForFilter(ContextualMessage message, String filterName) {
        return message.getConfiguration().getAvailableFeatureFilters().get(filterName.toLowerCase());
    }

}


