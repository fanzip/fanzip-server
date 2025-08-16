package org.example.fanzip.global.metric;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfig {

    @Bean
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Bean
    public JvmMemoryMetrics jvmMemoryMetrics(MeterRegistry meterRegistry){
        JvmMemoryMetrics jvmMemoryMetrics = new JvmMemoryMetrics();
        jvmMemoryMetrics.bindTo(meterRegistry);
        return jvmMemoryMetrics;
    }

    @Bean
    public JvmGcMetrics jvmGcMetrics(MeterRegistry meterRegistry){
        JvmGcMetrics jvmGcMetrics = new JvmGcMetrics();
        jvmGcMetrics.bindTo(meterRegistry);
        return jvmGcMetrics;
    }

    @Bean
    public JvmThreadMetrics jvmThreadMetrics(MeterRegistry meterRegistry){
        JvmThreadMetrics jvmThreadMetrics = new JvmThreadMetrics();
        jvmThreadMetrics.bindTo(meterRegistry);
        return jvmThreadMetrics;
    }

    @Bean
    public ProcessorMetrics processorMetrics(MeterRegistry meterRegistry){
        ProcessorMetrics processorMetrics = new ProcessorMetrics();
        processorMetrics.bindTo(meterRegistry);
        return processorMetrics;
    }

}
