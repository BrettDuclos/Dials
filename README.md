[![Build Status](https://travis-ci.org/BrettDuclos/Dials.svg?branch=master)](https://travis-ci.org/BrettDuclos/Dials)

Dials
=====

Dials is a java based 'Feature Toggle' library that allows for runtime decision making based on different sets of criteria. Dials allows for a combination of filters to be defined for a given feature, allowing for more customizable decision making.

Do you want your feature to only be active on business days, and during business hours? Applying a DayOfWeek filter and a TimeWindow filter would allow you to do just that.

Want to run a pilot for 10% of traffic during a 1 week period? Apply a Percentage or SeededPercentage filter and a DateRange filter.

As a part of the decision making process, Dials will maintain an execution context providing relevant information about what happened. You may see that the static data needed to run the filter was unable to apply because it is invalid. Or that it is the DateRange filter that is failing each run.


What's the deal with Dials?
---------------------------
Dials allows for more than just a simple yes or no when your features are executing. When a feature executes, the client can register back to the system that it has encountered an errored state. With this, we can calculate the rate of success for a feature. By defining an 'Increase Threshold' and a 'Decrease Threshold', a given filter can automatically be dialed up or down based on the configuration provided, thus the name Dials. This functionality is only currently implemented for percentage based filters, but will be expanded in the near future. Additionally, the 'Killswitch' will live alongside this, representing the threshold for turning the feature off completely.


Currently Dials is in its infancy, so many areas are currently limited in scope, but with plans to expand. 

Some current ideas are:
*   More filter options
*   More dialable filters
*   Better dial configuration and killswitch functionality
*   Providing a dropwizard service implementation for distrubuted systems
*   A simple interface for feature creation
*   Additional context recorders
*   Additional data stores
