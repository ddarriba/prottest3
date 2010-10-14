package es.uvigo.darwin.prottest.util.attributable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author rambaut
 *         Date: Nov 27, 2005
 *         Time: 1:31:50 PM
 */
public class AttributableHelper implements Attributable {

    @Override
	public void setAttribute(String name, Object value) {
		attributeMap.put(name, value);
	}

    @Override
	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

    @Override
    public void removeAttribute(String name) {
		attributeMap.remove(name);
	}

    @Override
    public Set<String> getAttributeNames() {
		return attributeMap.keySet();
	}

    @Override
	public Map<String, Object> getAttributeMap() {
		return Collections.unmodifiableMap(attributeMap);
	}

	Map<String, Object> attributeMap = new HashMap<String, Object>();
}