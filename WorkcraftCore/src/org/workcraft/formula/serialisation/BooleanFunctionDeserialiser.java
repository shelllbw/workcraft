/*
 *
 * Copyright 2008,2009 Newcastle University
 *
 * This file is part of Workcraft.
 *
 * Workcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Workcraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Workcraft.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.workcraft.formula.serialisation;

import org.w3c.dom.Element;
import org.workcraft.dom.hierarchy.NamespaceHelper;
import org.workcraft.exceptions.DeserialisationException;
import org.workcraft.formula.BooleanFormula;
import org.workcraft.formula.BooleanVariable;
import org.workcraft.formula.jj.BooleanFormulaParser;
import org.workcraft.formula.jj.ParseException;
import org.workcraft.serialisation.ReferenceResolver;
import org.workcraft.serialisation.xml.CustomXMLDeserialiser;
import org.workcraft.serialisation.xml.NodeFinaliser;
import org.workcraft.serialisation.xml.NodeInitialiser;
import org.workcraft.util.Func;

public abstract class BooleanFunctionDeserialiser implements CustomXMLDeserialiser {
    private static final class VariableResolver implements
            Func<String, BooleanVariable> {
        private final ReferenceResolver internalReferenceResolver;

        private VariableResolver(ReferenceResolver internalReferenceResolver) {
            this.internalReferenceResolver = internalReferenceResolver;
        }

        public BooleanVariable eval(String ref) {

            if (ref.startsWith("var_")) {
                ref = ref.substring("var_".length());

                BooleanVariable bv = (BooleanVariable) internalReferenceResolver.getObject(ref);
                if (bv != null) {
                    return bv;
                }

//                for (Object o: internalReferenceResolver.getObjects()) {
//                    if (o instanceof BooleanVariable) {
//                        bv = (BooleanVariable)o;
//                        if (bv.getLegacyID().equals(Integer.valueOf(ref)))
//                            return bv;
//
//                        return null;
//                    }
//                }
            }

            String hier = NamespaceHelper.flatToHierarchicalName(ref);

            return (BooleanVariable) internalReferenceResolver.getObject(hier);
        }
    }

    @Override
    public void finaliseInstance(Element element, Object instance, final ReferenceResolver internalReferenceResolver,
            ReferenceResolver externalReferenceResolver, NodeFinaliser nodeFinaliser) throws DeserialisationException {
        String attributeName = "formula";

        BooleanFormula formula = readFormulaFromAttribute(element, internalReferenceResolver, attributeName);

        setFormula(instance, formula);
    }

    public static BooleanFormula readFormulaFromAttribute(Element element, final ReferenceResolver internalReferenceResolver,
            String attributeName) throws DeserialisationException {
        String string = element.getAttribute(attributeName);

        BooleanFormula formula = null;
        try {
            if (!string.isEmpty()) {
                formula = BooleanFormulaParser.parse(string, new VariableResolver(internalReferenceResolver));
            }
        } catch (ParseException e) {
            throw new DeserialisationException(e);
        }
        return formula;
    }

    protected abstract void setFormula(Object deserialisee, BooleanFormula formula);

    @Override
    public void initInstance(Element element, Object instance, ReferenceResolver externalReferenceResolver,
            NodeInitialiser nodeInitialiser) throws DeserialisationException {

    }
}
