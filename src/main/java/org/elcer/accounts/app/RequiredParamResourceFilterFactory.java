package org.elcer.accounts.app;

import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Provider validating required parameters at the request time
 *
 * @see Required
 */
@Provider
public class RequiredParamResourceFilterFactory implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        final Method resourceMethod = resourceInfo.getResourceMethod();
        Required ann = resourceMethod.getAnnotation(Required.class);
        if (ann != null)
            context.register(new RequiredParamFilter(ann.value(), resourceInfo));
    }

    private class RequiredParamFilter implements ContainerRequestFilter {

        private final List<String> requiredParams;

        private RequiredParamFilter(String[] requiredParams, ResourceInfo resourceInfo) {
            this.requiredParams = Arrays.stream(requiredParams).collect(Collectors.toList());
            if (!Arrays.stream(resourceInfo.getResourceMethod().getParameters())
                    .map(r -> r.getAnnotation(Path.class))
                    .filter(Objects::nonNull)
                    .map(Path::value)
                    .allMatch(this.requiredParams::contains)) {
                throw new IllegalArgumentException("@Required params should refer to @Path params");
            }
        }

        @Override
        public void filter(ContainerRequestContext containerRequest) {
            var requiredParametersValueMissing = new ArrayList<String>();
            var queryParameters = containerRequest.getUriInfo().getQueryParameters();

            var urlParms = queryParameters.keySet();
            for (var param : queryParameters.entrySet()) {
                if (param.getValue().stream().allMatch(StringUtils::isEmpty) && requiredParams.contains(param.getKey()))
                    requiredParametersValueMissing.add(param.getKey());
            }

            requiredParams.stream().filter(r -> !urlParms.contains(r))
                    .forEach(requiredParametersValueMissing::add);

            if (!requiredParametersValueMissing.isEmpty()) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity(String.format("Parameter %s missing", String.join(",", requiredParametersValueMissing)))
                        .build());
            }
        }

    }

}