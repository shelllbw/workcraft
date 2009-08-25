package org.workcraft.framework;

import org.w3c.dom.Element;
import org.workcraft.dom.HierarchyNode;
import org.workcraft.framework.serialisation.ExternalReferenceResolver;

public interface VisualNodeSerialiser {
	public void serialise(HierarchyNode node, Element element, ExternalReferenceResolver referenceResolver);
}
