package com.maukaim.cryptohub.plugins.api.exchanges;

import com.maukaim.cryptohub.plugins.api.exchanges.model.ConnectionParameter;
import com.maukaim.cryptohub.plugins.api.plugin.PreProcess;

import java.util.List;
import java.util.Map;

public interface ExchangeServicePreProcess extends PreProcess {
    /**
     *  Used by ExchangeServices consumers to connect with it.
     * @return 1 or multiple solution to connect with this exchange.
     */
    public Map<String, List<ConnectionParameter>> getConnectionParameters();

}
