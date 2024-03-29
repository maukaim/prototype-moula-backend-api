package com.maukaim.blob.core.web;

import com.maukaim.blob.core.exchange.ExchangeServiceOrchestrator;
import com.maukaim.blob.core.exchange.ExchangeWrapper;
import com.maukaim.blob.plugins.api.exchanges.exception.ExchangeConnectionException;
import com.maukaim.blob.plugins.api.exchanges.exception.OrderTypeNotFoundException;
import com.maukaim.blob.plugins.api.exchanges.model.ConnectionParameter;
import com.maukaim.blob.plugins.api.exchanges.model.ConnectionParameters;
import com.maukaim.blob.plugins.api.exchanges.model.CryptoPair;
import com.maukaim.blob.plugins.core.model.module.ModuleInfo;
import com.maukaim.blob.plugins.core.model.module.ModuleProvider;
import com.maukaim.blob.plugins.api.exchanges.ExchangeService;
import com.maukaim.blob.plugins.api.order.Order;
import com.maukaim.blob.plugins.api.order.OrderParameter;
import com.maukaim.blob.core.web.view.ModuleInfoView;
import com.maukaim.blob.core.web.view.ModuleInfoViewFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/exchanges")
@Slf4j
public class ExchangeController {

    private final ExchangeServiceOrchestrator exchangeOrchestrator;

    public ExchangeController(
            @Autowired ExchangeServiceOrchestrator exchangeOrchestrator) {
        this.exchangeOrchestrator = exchangeOrchestrator;
    }


    @GetMapping(value = "/")
    public ResponseEntity<List<ModuleInfoView>> getExchanges() {
        List<ModuleInfo> availableExchangesInfo = this.exchangeOrchestrator.getAvailableExchangesInfo();
        List<ModuleInfoView> allExchangesAvailable = availableExchangesInfo.stream()
                .map(ModuleInfoViewFactory::build)
                .collect(Collectors.toList());
        return ResponseEntity.ok(allExchangesAvailable);
    }


    @GetMapping(value = "/connect/parameters")
    public ResponseEntity<Map<String, List<ConnectionParameter>>> getConnectionParameters(@RequestParam String pluginId,
                                                                                          @RequestParam String exchangeName) {
        Optional<ModuleProvider<? extends ExchangeService>> mpOptional = this.exchangeOrchestrator
                .getExchangeProvider(pluginId, exchangeName);
        if (mpOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            ModuleProvider<? extends ExchangeService> moduleProvider = mpOptional.get();
            return ResponseEntity.ok(this.exchangeOrchestrator.getConnectionParameters(moduleProvider));
        }
    }

    @PostMapping(value = "/connect")
    public ResponseEntity<String> connect(@RequestParam String pluginId,
                                          @RequestParam String exchangeName,
                                          @RequestBody ConnectionParameters parameters) {

        Optional<ModuleProvider<? extends ExchangeService>> mpOptional = this.exchangeOrchestrator
                .getExchangeProvider(pluginId, exchangeName);

        if (mpOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            ModuleProvider<? extends ExchangeService> moduleProvider = mpOptional.get();
            try {
                ExchangeWrapper exchangeWrapper = this.exchangeOrchestrator
                        .connect(moduleProvider, parameters);
                return ResponseEntity.ok(exchangeWrapper.getId());

            } catch (ExchangeConnectionException e) {
                return ResponseEntity.badRequest().build();
            }
        }

    }

    @PostMapping(value = "/{id}/orderTypes")
    public ResponseEntity<List<String>> getOrderTypes(@PathVariable("id") String wrapperId,
                                                      @RequestBody CryptoPair cryptoPair) {
        Optional<ExchangeWrapper> optionalExchangeWrapper = this.exchangeOrchestrator.getExchange(wrapperId);
        if (optionalExchangeWrapper.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(optionalExchangeWrapper.get().getService().getOrderTypes(cryptoPair));
        }
    }

    @GetMapping(value = "/{id}/cryptoPairs")
    public ResponseEntity<Set<CryptoPair>> getCryptoPairs(@PathVariable("id") String wrapperId) {
        Optional<ExchangeWrapper> optionalExchangeWrapper = this.exchangeOrchestrator.getExchange(wrapperId);
        if (optionalExchangeWrapper.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(optionalExchangeWrapper.get().getService().getAvailableSymbols());
        }
    }

    @PostMapping(value = "/{id}/orderParameters")
    public ResponseEntity<List<OrderParameter>> getOrderParameters(@PathVariable("id") String wrapperId,
                                                                   @RequestParam String orderType,
                                                                   @RequestBody CryptoPair cryptoPair) {
        Optional<ExchangeWrapper> optionalExchangeWrapper = this.exchangeOrchestrator.getExchange(wrapperId);
        if (optionalExchangeWrapper.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            try {
                return ResponseEntity.ok(optionalExchangeWrapper.get().getService().getOrderParameter(orderType, cryptoPair));
            } catch (OrderTypeNotFoundException e) {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @GetMapping(value = "/{id}/orders")
    public ResponseEntity<Collection<Order>> getExistingOrders(@PathVariable("id") String wrapperId) {
        Optional<ExchangeWrapper> optionalExchangeWrapper = this.exchangeOrchestrator.getExchange(wrapperId);
        if (optionalExchangeWrapper.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(optionalExchangeWrapper.get().getService().getExistingOrders());
        }
    }
}
