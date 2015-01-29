@if exist AI.jar goto update_label

jar cvfe AI.jar neetsdkasu.codevs4.AI -C classes/ .
@exit /b

:update_label

jar uvfe AI.jar neetsdkasu.codevs4.AI -C classes/ .
@exit /b

