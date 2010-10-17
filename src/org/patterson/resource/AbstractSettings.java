//
// Copyright (c) 2010 Waterman
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//

package org.patterson.resource;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class AbstractSettings implements Settings {

	private Map values;

	public AbstractSettings() {
		super();
	}

	@Override
	public Object getObjectAt(Object aKey) {
		return getObjectAt(aKey, null);
	}

	@Override
	public Object getObjectAt(Object aKey, Object aDefault) {
		init();
		Object tempObject = values.get(aKey);
		return tempObject == null ? aDefault : tempObject;
	}

	private void init() {
		if (values == null) {
			Map tempValues = new HashMap();
			initValues(tempValues);
			values = tempValues;
		}
	}

	protected abstract void initValues(Map someValues);

	@Override
	public String getStringAt(Object aKey, String aDefault) {
		Object tempString = getObjectAt(aKey, aDefault);
		return tempString == null ? null : tempString.toString();
	}

	@Override
	public String getStringAt(Object aKey) {
		return getStringAt(aKey, null);
	}

	@Override
	public Map<?, ?> asMap() {
		init();
		return values;
	}

}