/*
 * Copyright (C) 2025 American Printing House for the Blind
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.brailleblaster.utd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import nu.xom.Element;
import nu.xom.Node;
import org.brailleblaster.utd.actions.BoldAction;

import org.brailleblaster.utd.actions.GenericAction;
import org.brailleblaster.utd.actions.IAction;
import org.brailleblaster.utd.actions.SkipAction;
import org.brailleblaster.utd.matchers.INodeMatcher;
import org.brailleblaster.utd.matchers.XPathMatcher;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

public class ActionMultiMapTest {
    private ActionMultiMap multiActionMap;

    @BeforeMethod
    public void setupMethod() {
        multiActionMap = new ActionMultiMap();
    }

    @Test
    public void getAndSetActionMaps() {
        List<IActionMap> actionMaps = new ArrayList<>();
        multiActionMap.setMaps(actionMaps);
        assertSame(multiActionMap.getMaps(), actionMaps);
    }

    @DataProvider(name = "mapDataProvider")
    public Iterator<Object[]> mapDataProvider() {
        ActionMap sysActionMap = new ActionMap();
        sysActionMap.put(new XPathMatcher("s"), new GenericAction());
        ActionMap userActionMap = new ActionMap();
        userActionMap.put(new XPathMatcher("u"), new GenericAction());
        ActionMap documentActionMap = new ActionMap();
        documentActionMap.put(new XPathMatcher("d"), new GenericAction());
        INodeMatcher otherMatcher1 = new XPathMatcher("o1");
        INodeMatcher otherMatcher2 = new XPathMatcher("o2");
        INodeMatcher[] otherMatchers = new INodeMatcher[]{otherMatcher1, otherMatcher2};
        IAction[] otherActions = new IAction[]{new SkipAction(), new BoldAction()};
        List<Object[]> dataList = new ArrayList<>();
        dataList.add(new Object[]{sysActionMap, userActionMap, documentActionMap, otherMatchers, otherActions});
        dataList.add(new Object[]{sysActionMap, userActionMap, null, otherMatchers, otherActions});
        dataList.add(new Object[]{sysActionMap, null, documentActionMap, otherMatchers, otherActions});
        dataList.add(new Object[]{null, userActionMap, documentActionMap, otherMatchers, otherActions});
        dataList.add(new Object[]{new ActionMap(), new ActionMap(), new ActionMap(), otherMatchers, otherActions});
        dataList.add(new Object[]{null, null, null, otherMatchers, otherActions});
        return dataList.iterator();
    }

    @Test(dataProvider = "mapDataProvider")
    public void containsKey(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(documentActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(sysActionMap);
        List<INodeMatcher> knownMatchers = new ArrayList<>();
        if (sysActionMap != null) {
            knownMatchers.addAll(sysActionMap.keySet());
        }
        if (userActionMap != null) {
            knownMatchers.addAll(userActionMap.keySet());
        }
        if (documentActionMap != null) {
            knownMatchers.addAll(documentActionMap.keySet());
        }
        for (INodeMatcher matcher : knownMatchers) {
            assertTrue(multiActionMap.containsKey(matcher));
        }
        for (INodeMatcher matcher : otherMatchers) {
            assertFalse(multiActionMap.containsKey(matcher));
        }
    }

    private Set<Entry<INodeMatcher, IAction>> createCombinedEntrySet(ActionMap... actionMaps) {
        Set<Entry<INodeMatcher, IAction>> result = new HashSet<>();
        for (ActionMap actionMap : actionMaps) {
            if (actionMap != null) {
                result.addAll(actionMap.entrySet());
            }
        }
        return result;
    }

    @Test(dataProvider = "mapDataProvider")
    public void get(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(sysActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(documentActionMap);
        Set<Entry<INodeMatcher, IAction>> actionMaps = createCombinedEntrySet(sysActionMap, userActionMap, documentActionMap);
        for (Entry<INodeMatcher, IAction> entry : actionMaps) {
            assertSame(multiActionMap.get(entry.getKey()), entry.getValue());
        }
        for (INodeMatcher matcher : otherMatchers) {
            assertNull(multiActionMap.get(matcher));
        }
    }

    @Test(dataProvider = "mapDataProvider")
    public void size(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(sysActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(documentActionMap);
        int expectedSize = 0;
        if (sysActionMap != null) {
            expectedSize += sysActionMap.size();
        }
        if (userActionMap != null) {
            expectedSize += userActionMap.size();
        }
        if (documentActionMap != null) {
            expectedSize += documentActionMap.size();
        }
        assertEquals(multiActionMap.size(), expectedSize);
    }

    @Test(dataProvider = "mapDataProvider")
    public void isEmpty(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(sysActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(documentActionMap);
        boolean expected = multiActionMap.isEmpty();
        assertEquals(multiActionMap.isEmpty(), expected);
    }

    @Test(dataProvider = "mapDataProvider")
    public void keySet(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(sysActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(documentActionMap);
        Set<INodeMatcher> keys = new HashSet<>();
        if (sysActionMap != null) {
            keys.addAll(sysActionMap.keySet());
        }
        if (userActionMap != null) {
            keys.addAll(userActionMap.keySet());
        }
        if (documentActionMap != null) {
            keys.addAll(documentActionMap.keySet());
        }
        Set<INodeMatcher> keySet = multiActionMap.keySet();
        assertEquals(keySet.size(), keys.size());
        assertTrue(keys.containsAll(keySet));
        assertTrue(keySet.containsAll(keys));
    }

    @Test(dataProvider = "mapDataProvider")
    public void entrySet(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(documentActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(sysActionMap);
        Set<Entry<INodeMatcher, IAction>> entries = createCombinedEntrySet(sysActionMap, userActionMap, documentActionMap);
        Set<Entry<INodeMatcher, IAction>> entrySet = multiActionMap.entrySet();
        assertEquals(entrySet.size(), entries.size());
        assertTrue(entrySet.containsAll(entries));
        assertTrue(entries.containsAll(entrySet));
    }

    @Test(dataProvider = "mapDataProvider")
    public void values(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(sysActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(documentActionMap);
        List<IAction> expectedValues = new ArrayList<>();
        if (sysActionMap != null) {
            expectedValues.addAll(sysActionMap.values());
        }
        if (userActionMap != null) {
            expectedValues.addAll(userActionMap.values());
        }
        if (documentActionMap != null) {
            expectedValues.addAll(documentActionMap.values());
        }
        Collection<IAction> actualValues = multiActionMap.values();
        assertEquals(actualValues.size(), expectedValues.size());
        assertTrue(expectedValues.containsAll(actualValues));
        assertTrue(actualValues.containsAll(expectedValues));
    }

    @Test(dataProvider = "mapDataProvider")
    public void containsValue(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(sysActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(documentActionMap);
        List<IAction> values = new ArrayList<>();
        if (sysActionMap != null) {
            values.addAll(sysActionMap.values());
        }
        if (userActionMap != null) {
            values.addAll(userActionMap.values());
        }
        if (documentActionMap != null) {
            values.addAll(documentActionMap.values());
        }
        for (IAction action : values) {
            assertTrue(multiActionMap.containsValue(action));
        }
        for (IAction action : otherActions) {
            assertFalse(multiActionMap.containsValue(action), "On action " + action);
        }
    }

    @Test
    public void getDefaultAction() {
        assertNotNull(multiActionMap.getDefaultValue());
        assertTrue(multiActionMap.getDefaultValue() instanceof GenericAction);
    }

    @Test(dataProvider = "mapDataProvider")
    public void getNamespaces(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(sysActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(documentActionMap);
        NamespaceMap nsMap = new NamespaceMap();
        if (sysActionMap != null) {
            for (String prefix : sysActionMap.getNamespaces().getPrefixes()) {
                nsMap.addNamespace(prefix, sysActionMap.getNamespaces().getNamespace(prefix));
            }
        }
        if (userActionMap != null) {
            for (String prefix : userActionMap.getNamespaces().getPrefixes()) {
                nsMap.addNamespace(prefix, userActionMap.getNamespaces().getNamespace(prefix));
            }
        }
        if (documentActionMap != null) {
            for (String prefix : documentActionMap.getNamespaces().getPrefixes()) {
                nsMap.addNamespace(prefix, documentActionMap.getNamespaces().getNamespace(prefix));
            }
        }
        for (String prefix : nsMap.getPrefixes()) {
            assertTrue(multiActionMap.getNamespaces().getPrefixes().contains(prefix));
            assertEquals(multiActionMap.getNamespaces().getNamespace(prefix), nsMap.getNamespace(prefix));
        }
    }

    @Test(dataProvider = "mapDataProvider", expectedExceptions = {UnsupportedOperationException.class})
    public void put(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(sysActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(documentActionMap);
        multiActionMap.put(new XPathMatcher("//p"), new GenericAction());
    }

    @Test(dataProvider = "mapDataProvider", expectedExceptions = {UnsupportedOperationException.class})
    public void putAll(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(sysActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(documentActionMap);
        multiActionMap.putAll(new HashMap<>());
    }

    @Test(dataProvider = "mapDataProvider", expectedExceptions = {UnsupportedOperationException.class})
    public void remove(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(sysActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(documentActionMap);
        multiActionMap.remove(new XPathMatcher("//p"));
    }

    @Test(dataProvider = "mapDataProvider", expectedExceptions = {UnsupportedOperationException.class})
    public void clear(ActionMap sysActionMap, ActionMap userActionMap, ActionMap documentActionMap, INodeMatcher[] otherMatchers, IAction[] otherActions) {
        multiActionMap.getMaps().add(sysActionMap);
        multiActionMap.getMaps().add(userActionMap);
        multiActionMap.getMaps().add(documentActionMap);
        multiActionMap.clear();
    }

    @DataProvider(name = "findActionDataProvider")
    private Iterator<Boolean[]> findActionDataProvider() {
        List<Boolean[]> data = new ArrayList<>();
        data.add(new Boolean[]{Boolean.TRUE, Boolean.FALSE, Boolean.FALSE});
        data.add(new Boolean[]{Boolean.FALSE, Boolean.TRUE, Boolean.FALSE});
        data.add(new Boolean[]{null, Boolean.TRUE, Boolean.FALSE});
        data.add(new Boolean[]{Boolean.FALSE, Boolean.FALSE, Boolean.TRUE});
        data.add(new Boolean[]{null, Boolean.FALSE, Boolean.TRUE});
        data.add(new Boolean[]{null, null, Boolean.TRUE});
        data.add(new Boolean[]{Boolean.FALSE, Boolean.FALSE, Boolean.FALSE});
        data.add(new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.TRUE});
        return data.iterator();
    }

    @Test(dataProvider = "findActionDataProvider")
    public void findAction(Boolean sysHasMatch, Boolean userHasMatch, Boolean documentHasMatch) {
        IActionMap sysActionMap = null;
        IAction sysAction = mock(IAction.class);
        IActionMap userActionMap = null;
        IAction userAction = mock(IAction.class);
        IActionMap documentActionMap = null;
        IAction documentAction = mock(IAction.class);
        if (documentHasMatch != null) {
            documentActionMap = mock(IActionMap.class);
            if (documentHasMatch.equals(Boolean.TRUE)) {
                when(documentActionMap.findValueWithDefault(any(Node.class), isNull())).thenReturn(documentAction);
            }
        }
        multiActionMap.getMaps().add(documentActionMap);
        if (userHasMatch != null) {
            userActionMap = mock(IActionMap.class);
            if (userHasMatch.equals(Boolean.TRUE)) {
                when(userActionMap.findValueWithDefault(any(Node.class), isNull())).thenReturn(userAction);
            }
        }
        multiActionMap.getMaps().add(userActionMap);
        if (sysHasMatch != null) {
            sysActionMap = mock(IActionMap.class);
            if (sysHasMatch.equals(Boolean.TRUE)) {
                when(sysActionMap.findValueWithDefault(any(Node.class), isNull())).thenReturn(sysAction);
            }
        }
        multiActionMap.getMaps().add(sysActionMap);

        IAction defaultValue = new SkipAction();

        IAction result1 = multiActionMap.findValueOrDefault(new Element("e"));
        IAction result2 = multiActionMap.findValueWithDefault(new Element("e"), null);
        IAction result3 = multiActionMap.findValueWithDefault(new Element("e"), defaultValue);

        if (Boolean.TRUE.equals(documentHasMatch)) {
            assertSame(result1, documentAction);
            assertSame(result2, documentAction);
            assertSame(result3, documentAction);
        } else if (Boolean.TRUE.equals(userHasMatch)) {
            assertSame(result1, userAction);
            assertSame(result2, userAction);
            assertSame(result3, userAction);
        } else if (Boolean.TRUE.equals(sysHasMatch)) {
            assertSame(result1, sysAction);
            assertSame(result2, sysAction);
            assertSame(result3, sysAction);
        } else {
            assertEquals(result1, multiActionMap.getDefaultValue());
            assertNull(result2);
            assertEquals(result3, defaultValue);
        }
    }
}
