Required MVP
====

1. [v] Make selenium scenario "restart miner"
2. [] Create asic metrics reporter (need a history to see what is shown when "miner tempreture" is red)
3. [] Create metrics watcher (a minutes scheduled job). 
    It starts after SLEEP->NORMAL transition and work until miner temperature will be normal. If not --> trigger restart
