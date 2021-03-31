package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.test;

import java.nio.file.Paths;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.mockito.Mockito;

import de.tu_bs.cs.isf.e4cf.core.file_structure.WorkspaceFileSystem;
import de.tu_bs.cs.isf.e4cf.core.util.RCPContentProvider;

public class VATContextTest {

	private IEclipseContext eclipseCtx;

	private WorkspaceFileSystem fs;

	public VATContextTest() {
		refreshContext();
	}
	
	public void refreshContext() {
		eclipseCtx = EclipseContextFactory.create();
		
		addMockToContext(IEventBroker.class);
		
		fs = addToContext(WorkspaceFileSystem.class);
		String path = RCPContentProvider.getCurrentWorkspacePath();
		fs.initializeFileTree(path);
	}
	
	/**
	 * Adds mocked instance of class to the context.
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public <T> T addMockToContext(Class<T> clazz) {
		T mock = Mockito.mock(clazz);
		eclipseCtx.set(clazz, mock);
		return mock;	
	}
	
	/**
	 * Adds mocked instance of class as super class to the context.
	 * 
	 * @param <T> 
	 * @param <S> 
	 * @param clazz
	 * @param asSuper
	 * @return
	 */
	public <T extends S, S> S addMockToContext(Class<T> clazz, Class<S> asSuper) {
		T mock = Mockito.mock(clazz);
		eclipseCtx.set(asSuper, mock);
		return (S) mock;

	}
	
	/**
	 * Makes the instance of the class injected and known to the context.
	 * 
	 * @param <T>
	 * @param injectableClass
	 * @return
	 */
	public <T> T addToContext(Class<T> injectableClass) {
		T ctxObject = ContextInjectionFactory.make(injectableClass, eclipseCtx);
		eclipseCtx.set(injectableClass, ctxObject);
		return ctxObject;
	}
	
	/**
	 * Makes the instance of the class injected and known to the context.
	 * Injects the the injectable class as super class into the context, which
	 * is useful for e.g. storing concrete classes as interface in the context.
	 * 
	 * 
	 * @param <T>
	 * @param <S>
	 * @param injectableClass
	 * @param superClass
	 * @return
	 */
	public <T extends S, S> S addToContext(Class<T> injectableClass, Class<S> asSuper) {
		S injectableInstance = ContextInjectionFactory.make(injectableClass, eclipseCtx);
		eclipseCtx.set(asSuper, injectableInstance);
		return injectableInstance;
	}
	
	public String getResourcePath(String... pathComponents) {
		return Paths.get("resources", pathComponents).toAbsolutePath().toString();
	}
	
	public IEclipseContext getEclipseCtx() {
		return eclipseCtx;
	}

	public void setEclipseCtx(IEclipseContext eclipseCtx) {
		this.eclipseCtx = eclipseCtx;
	}
	
	public WorkspaceFileSystem getFs() {
		return fs;
	}
}
