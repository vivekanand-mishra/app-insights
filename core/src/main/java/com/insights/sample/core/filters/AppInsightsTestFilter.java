package com.insights.sample.core.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.applicationinsights.TelemetryConfiguration;
import com.microsoft.applicationinsights.extensibility.TelemetryInitializer;
import com.microsoft.applicationinsights.extensibility.TelemetryModule;
import com.microsoft.applicationinsights.internal.logger.InternalLogger;
import com.microsoft.applicationinsights.web.extensibility.initializers.WebOperationIdTelemetryInitializer;
import com.microsoft.applicationinsights.web.extensibility.initializers.WebOperationNameTelemetryInitializer;
import com.microsoft.applicationinsights.web.extensibility.initializers.WebSessionTelemetryInitializer;
import com.microsoft.applicationinsights.web.extensibility.initializers.WebUserAgentTelemetryInitializer;
import com.microsoft.applicationinsights.web.extensibility.initializers.WebUserTelemetryInitializer;
import com.microsoft.applicationinsights.web.extensibility.modules.WebRequestTrackingTelemetryModule;
import com.microsoft.applicationinsights.web.internal.WebRequestTrackingFilter;

@Component(configurationPid = "com.insights.sample.core.filters.AppInsightsConfigFilter", service = { Filter.class },
        property = { "sling.filter.scope=request", "sling.filter.pattern=/content/(.*).html(.*)",
                "service.ranking:Integer=" + Integer.MIN_VALUE })
public class AppInsightsTestFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(AppInsightsTestFilter.class);

    private WebRequestTrackingFilter webRequestTrackingFilter;

    @Override
    public void destroy() {
        LOG.debug("Destroy method of the Request Filter Invoked");
        webRequestTrackingFilter.destroy();

    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {
        webRequestTrackingFilter.doFilter(servletRequest, servletResponse, filterChain);
    }

    @Override
    public void init(final FilterConfig arg0) throws ServletException {
        Map<String, String> settings = new HashMap<>();
        settings.put("Level", "trace");
        settings.put("UniquePrefix", "app-insight");
        // Logs are cerated here- C:\Users\<usrid>\AppData\Local\Temp
        settings.put("BaseFolder", "app-insight-web-logs");

        InternalLogger.INSTANCE.initialize("file", settings);
        LOG.debug("Init method of the Request Filter");
        webRequestTrackingFilter = new WebRequestTrackingFilter("aemdev");
        TelemetryConfiguration.getActive().setInstrumentationKey("xxxx2953-xxxx-xxxx-xxxx-848fd147xxxx");
        List<TelemetryInitializer> initializers = new ArrayList<>();
        initializers.add(new WebOperationIdTelemetryInitializer());
        initializers.add(new WebOperationNameTelemetryInitializer());
        initializers.add(new WebSessionTelemetryInitializer());
        initializers.add(new WebUserTelemetryInitializer());
        initializers.add(new WebUserAgentTelemetryInitializer());
        TelemetryConfiguration.getActive().getTelemetryInitializers().addAll(initializers);
        TelemetryModule module = new WebRequestTrackingTelemetryModule();
        module.initialize(TelemetryConfiguration.getActive());
        TelemetryConfiguration.getActive().getTelemetryModules().add(module);
        webRequestTrackingFilter.init(arg0);

    }

}
