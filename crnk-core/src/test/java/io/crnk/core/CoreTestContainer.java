package io.crnk.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crnk.core.boot.CrnkBoot;
import io.crnk.core.engine.http.HttpRequestContextBase;
import io.crnk.core.engine.http.HttpRequestContextProvider;
import io.crnk.core.engine.internal.document.mapper.DocumentMapper;
import io.crnk.core.engine.internal.http.HttpRequestContextBaseAdapter;
import io.crnk.core.engine.query.QueryContext;
import io.crnk.core.engine.registry.RegistryEntry;
import io.crnk.core.engine.registry.ResourceRegistry;
import io.crnk.core.engine.url.ConstantServiceUrlProvider;
import io.crnk.core.mock.MockConstants;
import io.crnk.core.mock.repository.MockRepositoryUtil;
import io.crnk.core.module.Module;
import io.crnk.core.module.ModuleRegistry;
import io.crnk.core.module.discovery.ReflectionsServiceDiscovery;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.queryspec.internal.QuerySpecAdapter;
import org.mockito.Mockito;

public class CoreTestContainer {

	public static final String BASE_URL = "http://127.0.0.1";

	private CrnkBoot boot = new CrnkBoot();

	private QueryContext queryContext;

	private HttpRequestContextBase requestContextBase;

	private HttpRequestContextBaseAdapter requestContext;

	public CoreTestContainer() {
		MockRepositoryUtil.clear();
		boot.setServiceUrlProvider(new ConstantServiceUrlProvider(BASE_URL));
	}

	public void setDefaultPackage() {
		setPackage(MockConstants.TEST_MODELS_PACKAGE);
	}

	public void setPackage(String packageName) {
		boot.setServiceDiscovery(new ReflectionsServiceDiscovery(packageName));
	}

	public CrnkBoot getBoot() {
		return boot;
	}

	public void boot() {
		ModuleRegistry moduleRegistry = boot.getModuleRegistry();
		HttpRequestContextProvider httpRequestContextProvider = moduleRegistry.getHttpRequestContextProvider();

		requestContextBase = Mockito.mock(HttpRequestContextBase.class);
		requestContext = new HttpRequestContextBaseAdapter(requestContextBase);
		queryContext = requestContext.getQueryContext();
		queryContext.setBaseUrl(boot.getServiceUrlProvider().getUrl());

		boot.boot();
		httpRequestContextProvider.onRequestStarted(requestContext);
	}

	public QueryContext getQueryContext() {
		return queryContext;
	}

	public QuerySpecAdapter toQueryAdapter(QuerySpec querySpec) {
		ResourceRegistry resourceRegistry = boot.getResourceRegistry();
		return new QuerySpecAdapter(querySpec, resourceRegistry, queryContext);
	}

	public void addModule(Module module) {
		boot.addModule(module);
	}

	public RegistryEntry getEntry(Class resourceClass) {
		return boot.getResourceRegistry().getEntry(resourceClass);
	}

	public ModuleRegistry getModuleRegistry() {
		return boot.getModuleRegistry();
	}

	public ResourceRegistry getResourceRegistry() {
		return boot.getResourceRegistry();
	}

	public ObjectMapper getObjectMapper() {
		return boot.getObjectMapper();
	}

	public DocumentMapper getDocumentMapper() {
		return boot.getDocumentMapper();
	}

	public HttpRequestContextBase getRequestContextBase() {
		return requestContextBase;
	}

	public HttpRequestContextBaseAdapter getRequestContext() {
		return requestContext;
	}

	public RegistryEntry getEntry(String resourceType) {
		return getResourceRegistry().getEntry(resourceType);
	}
}
