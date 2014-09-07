package com.eclipsesource.v8.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class V8CallbackTest {

    private V8 v8;

    @Before
    public void seutp() {
        v8 = V8.createV8Runtime();
    }

    @After
    public void tearDown() {
        try {
            v8.release();
            if (V8.getActiveRuntimes() != 0) {
                throw new IllegalStateException("V8Runtimes not properly released.");
            }
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public interface ICallback {

        public Object unsupportedMethod();

        public void voidMethodNoParameters();

        public void voidMethodWithParameters(final int a, final double b, final boolean c, final String d);

        public void voidMethodWithArrayParameter(final V8Array array);

        public void voidMethodWithObjectParameter(final V8Object object);

        public int intMethodNoParameters();

        public Integer integerMethod();

        public int intMethodWithParameters(final int x, final int b);

        public int intMethodWithArrayParameter(final V8Array array);

        public double doubleMethodNoParameters();

        public double doubleMethodWithParameters(final double x, final double y);

        public boolean booleanMethodNoParameters();

        public boolean booleanMethodWithArrayParameter(final V8Array array);

        public String stringMethodNoParameters();

        public String stringMethodWithArrayParameter(final V8Array array);

        public V8Object v8ObjectMethodNoParameters();

        public V8Object v8ObjectMethodWithObjectParameter(final V8Object person);

    }

    @Test
    public void testUnsupportedReturnType() {
        ICallback callback = mock(ICallback.class);
        try {
            v8.registerJavaMethod(callback, "unsupportedMethod", "foo", new Class<?>[0]);
        } catch (IllegalStateException e) {
            assertEquals("Unsupported Return Type", e.getMessage());
            return;
        }
        fail("Exception should have been thrown.");
    }

    @Test
    public void testVoidMethodCalledFromVoidScript() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);

        v8.executeVoidScript("foo();");

        verify(callback).voidMethodNoParameters();
    }

    @Test
    public void testIntMethodCalledFromVoidScript() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "intMethodNoParameters", "foo", new Class<?>[0]);

        v8.executeVoidScript("foo();");

        verify(callback).intMethodNoParameters();
    }

    @Test
    public void testIntMethodCalledFromScriptWithResult() {
        ICallback callback = mock(ICallback.class);
        doReturn(7).when(callback).intMethodNoParameters();
        v8.registerJavaMethod(callback, "intMethodNoParameters", "foo", new Class<?>[0]);

        int result = v8.executeIntScript("foo();");

        assertEquals(7, result);
    }

    @Test
    public void testIntegerMethodCalledFromScriptWithResult() {
        ICallback callback = mock(ICallback.class);
        doReturn(8).when(callback).integerMethod();
        v8.registerJavaMethod(callback, "integerMethod", "foo", new Class<?>[0]);

        int result = v8.executeIntScript("foo();");

        assertEquals(8, result);
    }

    @Test
    public void testDoubleMethodCalledFromVoidScript() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "doubleMethodNoParameters", "foo", new Class<?>[0]);

        v8.executeVoidScript("foo();");

        verify(callback).doubleMethodNoParameters();
    }

    @Test
    public void testDoubleMethodCalledFromScriptWithResult() {
        ICallback callback = mock(ICallback.class);
        doReturn(3.14159).when(callback).doubleMethodNoParameters();
        v8.registerJavaMethod(callback, "doubleMethodNoParameters", "foo", new Class<?>[0]);

        double result = v8.executeDoubleScript("foo();");

        assertEquals(3.14159, result, 0.0000001);
    }

    @Test
    public void testBooleanMethodCalledFromVoidScript() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "booleanMethodNoParameters", "foo", new Class<?>[0]);

        v8.executeVoidScript("foo();");

        verify(callback).booleanMethodNoParameters();
    }

    @Test
    public void testBooleanMethodCalledFromScriptWithResult() {
        ICallback callback = mock(ICallback.class);
        doReturn(true).when(callback).booleanMethodNoParameters();
        v8.registerJavaMethod(callback, "booleanMethodNoParameters", "foo", new Class<?>[0]);

        boolean result = v8.executeBooleanScript("foo();");

        assertTrue(result);
    }

    @Test
    public void testStringMethodCalledFromVoidScript() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "stringMethodNoParameters", "foo", new Class<?>[0]);

        v8.executeVoidScript("foo();");

        verify(callback).stringMethodNoParameters();
    }

    @Test
    public void testStringMethodCalledFromScriptWithResult() {
        ICallback callback = mock(ICallback.class);
        doReturn("bar").when(callback).stringMethodNoParameters();
        v8.registerJavaMethod(callback, "stringMethodNoParameters", "foo", new Class<?>[0]);

        String result = v8.executeStringScript("foo();");

        assertEquals("bar", result);
    }

    @Test
    public void testStringMethodCalledFromScriptWithUndefined() {
        ICallback callback = mock(ICallback.class);
        doReturn(null).when(callback).stringMethodNoParameters();
        v8.registerJavaMethod(callback, "stringMethodNoParameters", "foo", new Class<?>[0]);

        boolean result = v8.executeBooleanScript("typeof foo() === 'undefined'");

        assertTrue(result);
    }

    @Test
    public void testV8ObjectMethodCalledFromVoidScript() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);

        v8.executeVoidScript("foo();");

        verify(callback).v8ObjectMethodNoParameters();
    }

    @Test
    public void testV8ObjectMethodReturnsUndefined() {
        ICallback callback = mock(ICallback.class);
        doReturn(null).when(callback).v8ObjectMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);

        boolean result = v8.executeBooleanScript("typeof foo() === 'undefined'");

        assertTrue(result);
    }

    @Test
    public void testV8ObjectMethodCalledFromScriptWithResult() {
        ICallback callback = mock(ICallback.class);
        V8Object object = new V8Object(v8);
        object.add("name", "john");
        doReturn(object).when(callback).v8ObjectMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);

        V8Object result = v8.executeObjectScript("foo();");

        assertEquals("john", result.getString("name"));
        result.release();
    }

    @Test
    public void testV8ObjectMethodReleasesResults() {
        ICallback callback = mock(ICallback.class);
        V8Object object = new V8Object(v8);
        doReturn(object).when(callback).v8ObjectMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);

        v8.executeVoidScript("foo();");

        assertTrue(object.isReleased());
    }

    @Test
    public void testVoidFunctionCallOnJSObject() {
        ICallback callback = mock(ICallback.class);
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);

        v8Object.executeVoidFunction("foo", null);

        verify(callback).voidMethodNoParameters();
        v8Object.release();
    }

    @Test
    public void testIntFunctionCallOnJSObject() {
        ICallback callback = mock(ICallback.class);
        doReturn(99).when(callback).intMethodNoParameters();
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "intMethodNoParameters", "foo", new Class<?>[0]);

        int result = v8Object.executeIntFunction("foo", null);

        verify(callback).intMethodNoParameters();
        assertEquals(99, result);
        v8Object.release();
    }

    @Test
    public void testDoubleFunctionCallOnJSObject() {
        ICallback callback = mock(ICallback.class);
        doReturn(99.9).when(callback).doubleMethodNoParameters();
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "doubleMethodNoParameters", "foo", new Class<?>[0]);

        double result = v8Object.executeDoubleFunction("foo", null);

        verify(callback).doubleMethodNoParameters();
        assertEquals(99.9, result, 0.000001);
        v8Object.release();
    }

    @Test
    public void testBooleanFunctionCallOnJSObject() {
        ICallback callback = mock(ICallback.class);
        doReturn(false).when(callback).booleanMethodNoParameters();
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "booleanMethodNoParameters", "foo", new Class<?>[0]);

        boolean result = v8Object.executeBooleanFunction("foo", null);

        verify(callback).booleanMethodNoParameters();
        assertFalse(result);
        v8Object.release();
    }

    @Test
    public void testStringFunctionCallOnJSObject() {
        ICallback callback = mock(ICallback.class);
        doReturn("mystring").when(callback).stringMethodNoParameters();
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "stringMethodNoParameters", "foo", new Class<?>[0]);

        String result = v8Object.executeStringFunction("foo", null);

        verify(callback).stringMethodNoParameters();
        assertEquals("mystring", result);
        v8Object.release();
    }

    @Test
    public void testV8ObjectFunctionCallOnJSObject() {
        ICallback callback = mock(ICallback.class);
        doReturn(v8.executeObjectScript("x = {first:'bob'}; x")).when(callback).v8ObjectMethodNoParameters();
        V8Object v8Object = new V8Object(v8);
        v8Object.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[0]);

        V8Object result = v8Object.executeObjectFunction("foo", null);

        verify(callback).v8ObjectMethodNoParameters();
        assertEquals("bob", result.getString("first"));
        v8Object.release();
        result.release();
    }

    @Test
    public void testVoidMethodCalledFromIntScript() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);

        v8.executeIntScript("foo();1");

        verify(callback).voidMethodNoParameters();
    }

    @Test
    public void testVoidMethodCalledFromDoubleScript() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);

        v8.executeDoubleScript("foo();1.1");

        verify(callback).voidMethodNoParameters();
    }

    @Test
    public void testVoidMethodCalledFromStringScript() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);

        v8.executeStringScript("foo();'test'");

        verify(callback).voidMethodNoParameters();
    }

    @Test
    public void testVoidMethodCalledFromArrayScript() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);

        v8.executeArrayScript("foo();[]").release();

        verify(callback).voidMethodNoParameters();
    }

    @Test
    public void testVoidMethodCalledFromObjectScript() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[0]);

        v8.executeObjectScript("foo(); bar={}; bar;").release();

        verify(callback).voidMethodNoParameters();
    }

    @Test
    public void testVoidMethodCalledWithParameters() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithParameters", "foo", new Class<?>[] { Integer.TYPE, Double.TYPE,
                Boolean.TYPE, String.class });

        v8.executeVoidScript("foo(1,1.1, false, 'string');");

        verify(callback).voidMethodWithParameters(1, 1.1, false, "string");
    }

    @Test
    public void testIntMethodCalledWithParameters() {
        ICallback callback = mock(ICallback.class);
        doAnswer(new Answer<Integer>() {

            @Override
            public Integer answer(final InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                int x = (int) args[0];
                int y = (int) args[1];
                return x + y;
            }

        }).when(callback).intMethodWithParameters(anyInt(), anyInt());
        v8.registerJavaMethod(callback, "intMethodWithParameters", "foo", new Class<?>[] { Integer.TYPE, Integer.TYPE });

        int result = v8.executeIntScript("foo(8,7);");

        verify(callback).intMethodWithParameters(8, 7);
        assertEquals(15, result);
    }

    @Test
    public void testDoubleMethodCalledWithParameters() {
        ICallback callback = mock(ICallback.class);
        doAnswer(new Answer<Double>() {

            @Override
            public Double answer(final InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                double x = (double) args[0];
                double y = (double) args[1];
                return x + y;
            }

        }).when(callback).doubleMethodWithParameters(anyInt(), anyInt());
        v8.registerJavaMethod(callback, "doubleMethodWithParameters", "foo",
                new Class<?>[] { Double.TYPE, Double.TYPE });

        double result = v8.executeDoubleScript("foo(8.3,7.1);");

        verify(callback).doubleMethodWithParameters(8.3, 7.1);
        assertEquals(15.4, result, 0.000001);
    }

    @Test
    public void testVoidMethodCalledWithArrayParameters() {
        ICallback callback = mock(ICallback.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                assertEquals(1, args.length);
                assertEquals(1, ((V8Array) args[0]).getInteger(0));
                assertEquals(2, ((V8Array) args[0]).getInteger(1));
                assertEquals(3, ((V8Array) args[0]).getInteger(2));
                assertEquals(4, ((V8Array) args[0]).getInteger(3));
                assertEquals(5, ((V8Array) args[0]).getInteger(4));
                return null;
            }
        }).when(callback).voidMethodWithArrayParameter(any(V8Array.class));
        v8.registerJavaMethod(callback, "voidMethodWithArrayParameter", "foo", new Class<?>[] { V8Array.class });

        v8.executeVoidScript("foo([1,2,3,4,5]);");
    }

    @Test
    public void testIntMethodCalledWithArrayParameters() {
        ICallback callback = mock(ICallback.class);
        doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(final InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                int arrayLength = ((V8Array) args[0]).getSize();
                int result = 0;
                for (int i = 0; i < arrayLength; i++) {
                    result += ((V8Array) args[0]).getInteger(i);
                }
                return result;
            }
        }).when(callback).intMethodWithArrayParameter(any(V8Array.class));
        v8.registerJavaMethod(callback, "intMethodWithArrayParameter", "foo", new Class<?>[] { V8Array.class });

        int result = v8.executeIntScript("foo([1,2,3,4,5]);");

        assertEquals(15, result);
    }

    @Test
    public void testBooleanMethodCalledWithArrayParameters() {
        ICallback callback = mock(ICallback.class);
        doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(final InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                int arrayLength = ((V8Array) args[0]).getSize();
                int result = 0;
                for (int i = 0; i < arrayLength; i++) {
                    result += ((V8Array) args[0]).getInteger(i);
                }
                return result > 10;
            }
        }).when(callback).booleanMethodWithArrayParameter(any(V8Array.class));
        v8.registerJavaMethod(callback, "booleanMethodWithArrayParameter", "foo", new Class<?>[] { V8Array.class });

        boolean result = v8.executeBooleanScript("foo([1,2,3,4,5]);");

        assertTrue(result);
    }

    @Test
    public void testArrayMethodCalledWithArrayParameters() {
        ICallback callback = mock(ICallback.class);
        doAnswer(new Answer<String>() {
            @Override
            public String answer(final InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                int arrayLength = ((V8Array) args[0]).getSize();
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < arrayLength; i++) {
                    result.append(((V8Array) args[0]).getString(i));
                }
                return result.toString();
            }
        }).when(callback).stringMethodWithArrayParameter(any(V8Array.class));
        v8.registerJavaMethod(callback, "stringMethodWithArrayParameter", "foo", new Class<?>[] { V8Array.class });

        String result = v8.executeStringScript("foo(['a', 'b', 'c', 'd', 'e']);");

        assertEquals("abcde", result);
    }

    @Test
    public void testVoidMethodCalledWithObjectParameters() {
        ICallback callback = mock(ICallback.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                assertEquals(1, args.length);
                assertEquals("john", ((V8Object) args[0]).getString("first"));
                assertEquals("smith", ((V8Object) args[0]).getString("last"));
                assertEquals(7, ((V8Object) args[0]).getInteger("age"));
                return null;
            }
        }).when(callback).voidMethodWithObjectParameter(any(V8Object.class));
        v8.registerJavaMethod(callback, "voidMethodWithObjectParameter", "foo", new Class<?>[] { V8Object.class });

        v8.executeVoidScript("foo({first:'john', last:'smith', age:7});");

        verify(callback).voidMethodWithObjectParameter(any(V8Object.class));
    }

    @Test
    public void testObjectMethodCalledWithObjectParameters() {
        ICallback callback = mock(ICallback.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                V8Object parameter = ((V8Object) args[0]);
                V8Object result = new V8Object(v8);
                result.add("first", parameter.getString("last"));
                result.add("last", parameter.getString("first"));
                return result;
            }
        }).when(callback).v8ObjectMethodWithObjectParameter(any(V8Object.class));
        v8.registerJavaMethod(callback, "v8ObjectMethodWithObjectParameter", "foo", new Class<?>[] { V8Object.class });

        V8Object result = v8.executeObjectScript("foo({first:'john', last:'smith'});");

        assertEquals("smith", result.getString("first"));
        assertEquals("john", result.getString("last"));
        result.release();
    }

    @Test(expected = RuntimeException.class)
    public void testVoidMethodThrowsJavaException() {
        ICallback callback = mock(ICallback.class);
        doThrow(new RuntimeException("My Runtime Exception")).when(callback).voidMethodNoParameters();
        v8.registerJavaMethod(callback, "voidMethodNoParameters", "foo", new Class<?>[] {});

        try {
            v8.executeVoidScript("foo()");
        } catch (Exception e) {
            assertEquals("My Runtime Exception", e.getMessage());
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testIntMethodThrowsJavaException() {
        ICallback callback = mock(ICallback.class);
        doThrow(new RuntimeException("My Runtime Exception")).when(callback).intMethodNoParameters();
        v8.registerJavaMethod(callback, "intMethodNoParameters", "foo", new Class<?>[] {});

        try {
            v8.executeVoidScript("foo()");
        } catch (Exception e) {
            assertEquals("My Runtime Exception", e.getMessage());
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testDoubleMethodThrowsJavaException() {
        ICallback callback = mock(ICallback.class);
        doThrow(new RuntimeException("My Runtime Exception")).when(callback).doubleMethodNoParameters();
        v8.registerJavaMethod(callback, "doubleMethodNoParameters", "foo", new Class<?>[] {});

        try {
            v8.executeVoidScript("foo()");
        } catch (Exception e) {
            assertEquals("My Runtime Exception", e.getMessage());
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testBooleanMethodThrowsJavaException() {
        ICallback callback = mock(ICallback.class);
        doThrow(new RuntimeException("My Runtime Exception")).when(callback).booleanMethodNoParameters();
        v8.registerJavaMethod(callback, "booleanMethodNoParameters", "foo", new Class<?>[] {});

        try {
            v8.executeVoidScript("foo()");
        } catch (Exception e) {
            assertEquals("My Runtime Exception", e.getMessage());
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testStringMethodThrowsJavaException() {
        ICallback callback = mock(ICallback.class);
        doThrow(new RuntimeException("My Runtime Exception")).when(callback).stringMethodNoParameters();
        v8.registerJavaMethod(callback, "stringMethodNoParameters", "foo", new Class<?>[] {});

        try {
            v8.executeVoidScript("foo()");
        } catch (Exception e) {
            assertEquals("My Runtime Exception", e.getMessage());
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testV8ObjectMethodThrowsJavaException() {
        ICallback callback = mock(ICallback.class);
        doThrow(new RuntimeException("My Runtime Exception")).when(callback).v8ObjectMethodNoParameters();
        v8.registerJavaMethod(callback, "v8ObjectMethodNoParameters", "foo", new Class<?>[] {});

        try {
            v8.executeVoidScript("foo()");
        } catch (Exception e) {
            assertEquals("My Runtime Exception", e.getMessage());
            throw e;
        }
    }

    @Test
    public void testVoidMethodCallWithMissingObjectArgs() {
        ICallback callback = mock(ICallback.class);
        v8.registerJavaMethod(callback, "voidMethodWithObjectParameter", "foo", new Class<?>[] { V8Object.class });

        v8.executeVoidScript("foo()");

        verify(callback).voidMethodWithObjectParameter(null);
    }

}