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

package org.workcraft.plugins.petri;

import org.workcraft.annotations.VisualClass;
import org.workcraft.dom.math.MathNode;
import org.workcraft.exceptions.ArgumentException;
import org.workcraft.observation.PropertyChangedEvent;

@VisualClass(org.workcraft.plugins.petri.VisualPlace.class)
public class Place extends MathNode {
	protected int tokens = 0;
	protected int capacity = 1;

	public int getTokens() {
		return tokens;
	}

	public void setTokens(int tokens) {
		if (tokens < 0) {
			throw new ArgumentException("The number of tokens cannot be negative.");
		}
		if (tokens > capacity) {
			capacity = tokens;
		}
		this.tokens = tokens;
		sendNotification( new PropertyChangedEvent(this, "tokens") );
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		if (capacity < 1) {
			throw new ArgumentException("Negative or zero capacity is not allowed.");
		}
		if (tokens > capacity) {
			throw new ArgumentException("The place capacity "+ capacity + " is too small for the current number of tokens " + tokens + " .");
		}
		this.capacity = capacity;
		sendNotification ( new PropertyChangedEvent (this, "capacity"));
	}
}