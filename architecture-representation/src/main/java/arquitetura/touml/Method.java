package arquitetura.touml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import arquitetura.helpers.UtilResources;
import arquitetura.representation.Concern;


/**
 * 
 * @author edipofederle<edipofederle@gmail.com>
 *
 */
public class Method {
	
	private String id;
	private Method method;
	private VisibilityKind visibility;
	private String name;
	private List<Argument> arguments = new ArrayList<Argument>();
	private Types.Type typeReturn;
	private boolean isAbstract = false;
	private Set<Concern> concerns = new HashSet<Concern>();

	private Method(){}
	
	public Method withArguments(List<Argument> arguments) {
		this.arguments.addAll(arguments);
		return this;
	}


	public Method withVisibility(VisibilityKind visibility) {
		this.visibility = visibility;
		return this;
	}


	public Method withReturn(Types.Type typeReturn) {
		this.typeReturn = typeReturn;
		return this;
	}
	

	/**
	 * @return the isAbstract
	 */
	public String isAbstract() {
		return isAbstract ? "true" : "false";
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}
	

	/**
	 * @return the concerns
	 */
	public Set<Concern> getConcerns() {
		return Collections.unmodifiableSet(concerns);
	}

	/**
	 * @return the visibility
	 */
	public String getVisibility() {
		if(visibility == null)
			return "public";
		return visibility.getName();
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the arguments
	 */
	public List<Argument> getArguments() {
		return arguments;
	}


	/**
	 * @return the returnMethod
	 */
	public String getReturnMethod() {
		return typeReturn != null ? typeReturn.getName() : "";
	}


	public Method build() {
		if(this.id == null)
			setId(UtilResources.getRandonUUID());
		return this;
	}


	public static Method create() {
		Method method = new Method();
		return method;
	}

	private void setId(String randonUUID) {
		this.id = randonUUID;
	}

	public Method withName(String name) {
		this.name = name;
		return this;
	}

	public Method abstractMethod() {
		this.isAbstract = true;
		return this;
	}

	public String getId() {
		return this.id;
	}

	public Method withConcerns(Set<Concern> ownConcerns) {
		this.concerns  = ownConcerns;
		return this;
	}

	public Method withId(String id2) {
		this.id = id2;
		return this;
	}
	
}