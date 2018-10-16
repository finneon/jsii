package software.amazon.jsii.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import software.amazon.jsii.JsiiException;
import software.amazon.jsii.tests.calculator.AbstractClass;
import software.amazon.jsii.tests.calculator.AbstractClassReturner;
import software.amazon.jsii.tests.calculator.Add;
import software.amazon.jsii.tests.calculator.AllTypes;
import software.amazon.jsii.tests.calculator.AsyncVirtualMethods;
import software.amazon.jsii.tests.calculator.Calculator;
import software.amazon.jsii.tests.calculator.CalculatorProps;
import software.amazon.jsii.tests.calculator.DerivedStruct;
import software.amazon.jsii.tests.calculator.DoNotOverridePrivates;
import software.amazon.jsii.tests.calculator.DoubleTrouble;
import software.amazon.jsii.tests.calculator.GiveMeStructs;
import software.amazon.jsii.tests.calculator.IFriendlier;
import software.amazon.jsii.tests.calculator.IFriendlyRandomGenerator;
import software.amazon.jsii.tests.calculator.IInterfaceWithProperties;
import software.amazon.jsii.tests.calculator.IRandomNumberGenerator;
import software.amazon.jsii.tests.calculator.InterfaceImplementedByAbstractClass;
import software.amazon.jsii.tests.calculator.JSObjectLiteralForInterface;
import software.amazon.jsii.tests.calculator.JSObjectLiteralToNative;
import software.amazon.jsii.tests.calculator.JSObjectLiteralToNativeClass;
import software.amazon.jsii.tests.calculator.Multiply;
import software.amazon.jsii.tests.calculator.Negate;
import software.amazon.jsii.tests.calculator.NodeStandardLibrary;
import software.amazon.jsii.tests.calculator.NumberGenerator;
import software.amazon.jsii.tests.calculator.Polymorphism;
import software.amazon.jsii.tests.calculator.Power;
import software.amazon.jsii.tests.calculator.ReferenceEnumFromScopedPackage;
import software.amazon.jsii.tests.calculator.Statics;
import software.amazon.jsii.tests.calculator.Sum;
import software.amazon.jsii.tests.calculator.SyncVirtualMethods;
import software.amazon.jsii.tests.calculator.UnionProperties;
import software.amazon.jsii.tests.calculator.UsesInterfaceWithProperties;
import software.amazon.jsii.tests.calculator.composition.CompositeOperation;
import software.amazon.jsii.tests.calculator.lib.EnumFromScopedModule;
import software.amazon.jsii.tests.calculator.lib.IFriendly;
import software.amazon.jsii.tests.calculator.lib.MyFirstStruct;
import software.amazon.jsii.tests.calculator.lib.Number;
import software.amazon.jsii.tests.calculator.lib.StructWithOnlyOptionals;
import software.amazon.jsii.tests.calculator.lib.Value;
import software.amazon.jsii.tests.calculator.JavaReservedWords;
import software.amazon.jsii.tests.calculator.ClassWithPrivateConstructorAndAutomaticProperties;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ComplianceTest {
    /**
     * Verify that we can marshal and unmarshal objects without type information.
     */
    @Test
    public void primitiveTypes() throws IOException {
        AllTypes types = new AllTypes();

        // boolean
        types.setBooleanProperty(true);
        assertEquals(true, types.getBooleanProperty());

        // string
        types.setStringProperty("foo");
        assertEquals("foo", types.getStringProperty());

        // number
        types.setNumberProperty(1234);
        assertEquals(1234, types.getNumberProperty());

        // date
        types.setDateProperty(Instant.ofEpochMilli(123));
        assertEquals(Instant.ofEpochMilli(123), types.getDateProperty());

        // json
        types.setJsonProperty((ObjectNode) new ObjectMapper().readTree("{ \"Foo\": 123 }"));
        assertEquals(123, types.getJsonProperty().get("Foo").numberValue());
    }

    @Test
    public void dates() {
        AllTypes types = new AllTypes();

        // strong type
        types.setDateProperty(Instant.ofEpochMilli(123));
        assertEquals(Instant.ofEpochMilli(123), types.getDateProperty());

        // weak type
        types.setAnyProperty(Instant.ofEpochSecond(999));
        assertEquals(Instant.ofEpochSecond(999), types.getAnyProperty());
    }

    @Test
    public void collectionTypes() {
        AllTypes types = new AllTypes();

        // array
        types.setArrayProperty(Arrays.asList("Hello", "World"));
        assertEquals("World", types.getArrayProperty().get(1));

        // map
        Map<String, java.lang.Number> map = new HashMap<>();
        map.put("Foo", 123);
        types.setMapProperty(map);
    }

    @Test
    public void dynamicTypes() throws IOException {
        AllTypes types = new AllTypes();

        // boolean
        types.setAnyProperty(false);
        assertEquals(false, types.getAnyProperty());

        // string
        types.setAnyProperty("String");
        assertEquals("String", types.getAnyProperty());

        // number
        types.setAnyProperty(12);
        assertEquals(12, types.getAnyProperty());

        // date
        types.setAnyProperty(Instant.ofEpochSecond(1234));
        assertEquals(Instant.ofEpochSecond(1234), types.getAnyProperty());

        // json (notice that when deserialized, it is deserialized as a map).
        types.setAnyProperty(new ObjectMapper().readTree("{ \"Goo\": [ \"Hello\", { \"World\": 123 } ] }"));
        assertEquals(123, ((Map<?, ?>)((List<?>)((Map<?, ?>)types.getAnyProperty()).get("Goo")).get(1)).get("World"));

        // array
        types.setAnyProperty(Arrays.asList("Hello", "World"));
        assertEquals("Hello", ((List<?>)types.getAnyProperty()).get(0));
        assertEquals("World", ((List<?>)types.getAnyProperty()).get(1));

        // array of any
        types.setAnyArrayProperty(Arrays.asList("Hybrid", new Number(12), 123, false));
        assertEquals(123, types.getAnyArrayProperty().get(2));

        // map
        Map<String, Object> map = new HashMap<>();
        map.put("MapKey", "MapValue");
        types.setAnyProperty(map);
        assertEquals("MapValue", ((Map<?, ?>)types.getAnyProperty()).get("MapKey"));

        // map of any
        map.put("Goo", 19289812);
        types.setAnyMapProperty(map);
        assertEquals(19289812, types.getAnyMapProperty().get("Goo"));

        // classes
        Multiply mult = new Multiply(new Number(10), new Number(20));
        types.setAnyProperty(mult);
        assertSame(types.getAnyProperty(), mult);
        assertTrue(types.getAnyProperty() instanceof Multiply);
        assertEquals(200, ((Multiply) types.getAnyProperty()).getValue());
    }

    @Test
    public void unionTypes() {
        AllTypes types = new AllTypes();

        // single valued property
        types.setUnionProperty(1234);
        assertEquals(1234, types.getUnionProperty());

        types.setUnionProperty("Hello");
        assertEquals("Hello", types.getUnionProperty());

        types.setUnionProperty(new Multiply(new Number(2), new Number(12)));
        assertEquals(24, ((Multiply)types.getUnionProperty()).getValue());

        // NOTE: union collections are untyped in Java (java.lang.Object)

        // map
        Map<String, Object> map = new HashMap<>();
        map.put("Foo", new Multiply(new Number(2), new Number(99)));
        types.setUnionMapProperty(map);

        // array
        types.setUnionArrayProperty(Arrays.asList("Hello", 123, new Number(33)));
        assertEquals(33, ((Number)((List<?>)types.getUnionArrayProperty()).get(2)).getValue());
    }



    @Test
    public void createObjectAndCtorOverloads() {
        new Calculator();
        new Calculator(CalculatorProps.builder().withMaximumValue(10).build());
    }

    @Test
    public void getSetPrimitiveProperties() {
        Number number = new Number(20);
        assertEquals(20, number.getValue());
        assertEquals(40, number.getDoubleValue());
        assertEquals(-30, new Negate(new Add(new Number(20), new Number(10))).getValue());
        assertEquals(20, new Multiply(new Add(new Number(5), new Number(5)), new Number(2)).getValue());
        assertEquals(3 * 3 * 3 * 3, new Power(new Number(3), new Number(4)).getValue());
        assertEquals(999, new Power(new Number(999), new Number(1)).getValue());
        assertEquals(1, new Power(new Number(999), new Number(0)).getValue());
    }

    @Test
    public void callMethods() {
        Calculator calc = new Calculator();
        calc.add(10); assertEquals(10, calc.getValue());
        calc.mul(2); assertEquals(20, calc.getValue());
        calc.pow(5); assertEquals(20 * 20 * 20 * 20 * 20, calc.getValue());
        calc.neg(); assertEquals(-3200000, calc.getValue());
    }

    @Test
    public void unmarshallIntoAbstractType() {
        Calculator calc = new Calculator();
        calc.add(120);
        Value value = calc.getCurr();
        assertEquals(120, value.getValue());
    }

    @Test
    public void getAndSetNonPrimitiveProperties() {
        Calculator calc = new Calculator();
        calc.add(3200000);
        calc.neg();
        calc.setCurr(new Multiply(new Number(2), calc.getCurr()));
        assertEquals(-6400000, calc.getValue());
    }

    @Test
    public void getAndSetEnumValues() {
        Calculator calc = new Calculator();
        calc.add(9);
        calc.pow(3);
        assertEquals(CompositeOperation.CompositionStringStyle.Normal, calc.getStringStyle());
        calc.setStringStyle(CompositeOperation.CompositionStringStyle.Decorated);
        assertEquals(CompositeOperation.CompositionStringStyle.Decorated, calc.getStringStyle());
        assertEquals("<<[[{{(((1 * (0 + 9)) * (0 + 9)) * (0 + 9))}}]]>>", calc.toString());
    }

    @Test
    public void useEnumFromScopedModule() {
        ReferenceEnumFromScopedPackage obj = new ReferenceEnumFromScopedPackage();
        assertEquals(EnumFromScopedModule.Value2, obj.getFoo());
        obj.setFoo(EnumFromScopedModule.Value1);
        assertEquals(EnumFromScopedModule.Value1, obj.loadFoo());
        obj.saveFoo(EnumFromScopedModule.Value2);
        assertEquals(EnumFromScopedModule.Value2, obj.getFoo());
    }

    @Test
    public void undefinedAndNull() {
        Calculator calculator = new Calculator();
        assertNull(calculator.getMaxValue());
        calculator.setMaxValue(null);
    }

    @Test
    public void arrays() {
        Sum sum = new Sum();
        sum.setParts(Arrays.asList(new Number(5), new Number(10), new Multiply(new Number(2), new Number(3))));
        assertEquals(10 + 5 + (2 * 3), sum.getValue());
        assertEquals(5, sum.getParts().get(0).getValue());
        assertEquals(6, sum.getParts().get(2).getValue());
        assertEquals("(((0 + 5) + 10) + (2 * 3))", sum.toString());
    }

    @Test
    public void maps() {
        Calculator calc2 = new Calculator(); // Initializer overload (props is optional)
        calc2.add(10);
        calc2.add(20);
        calc2.mul(2);
        assertEquals(2, calc2.getOperationsMap().get("add").size());
        assertEquals(1, calc2.getOperationsMap().get("mul").size());
        assertEquals(30, calc2.getOperationsMap().get("add").get(1).getValue());
    }

    @Test
    public void fluentApi() {
        final Calculator calc3 = new Calculator(CalculatorProps.builder()
                .withInitialValue(20)
                .withMaximumValue(30)
                .build());
        calc3.add(3);
        assertEquals(23, calc3.getValue());
    }

    @Test
    public void unionPropertiesWithBuilder() throws Exception {

        // verify we have a withXxx overload for each union type
        UnionProperties.Builder builder = UnionProperties.builder();
        assertNotNull(builder.getClass().getMethod("withBar", java.lang.Number.class));
        assertNotNull(builder.getClass().getMethod("withBar", String.class));
        assertNotNull(builder.getClass().getMethod("withBar", AllTypes.class));
        assertNotNull(builder.getClass().getMethod("withFoo", String.class));
        assertNotNull(builder.getClass().getMethod("withFoo", java.lang.Number.class));

        UnionProperties obj1 = UnionProperties.builder()
            .withBar(12)
            .withFoo("Hello")
            .build();
        assertEquals(12, obj1.getBar());
        assertEquals("Hello", obj1.getFoo());

        // verify we have a setXxx for each type
        assertNotNull(obj1.getClass().getMethod("setFoo", String.class));
        assertNotNull(obj1.getClass().getMethod("setFoo", java.lang.Number.class));

        UnionProperties obj2 = UnionProperties.builder()
            .withBar("BarIsString")
            .build();
        assertEquals("BarIsString", obj2.getBar());
        assertNull(obj2.getFoo());

        AllTypes allTypes = new AllTypes();
        UnionProperties obj3 = UnionProperties.builder()
            .withBar(allTypes)
            .withFoo(999)
            .build();
        assertSame(allTypes, obj3.getBar());
        assertEquals(999, obj3.getFoo());
    }

    @Test
    public void exceptions() {
        final Calculator calc3 = new Calculator(CalculatorProps.builder()
            .withInitialValue(20)
            .withMaximumValue(30).build());
        calc3.add(3);
        assertEquals(23, calc3.getValue());
        boolean thrown = false;
        try { calc3.add(10); }
        catch (Exception e) { thrown = true; }
        assertTrue(thrown);
        calc3.setMaxValue(40);
        calc3.add(10);
        assertEquals(33, calc3.getValue());
    }

    @Test
    public void unionProperties() {
        Calculator calc3 = new Calculator();
        calc3.setUnionProperty(new Multiply(new Number(9), new Number(3)));
        assertTrue(calc3.getUnionProperty() instanceof Multiply);
        assertEquals(9 * 3, calc3.readUnionValue());
        calc3.setUnionProperty(new Power(new Number(10), new Number(3)));
        assertTrue(calc3.getUnionProperty() instanceof Power);
    }

    @Test
    public void subclassing() {
        Calculator calc = new Calculator();
        calc.setCurr(new AddTen(33));
        calc.neg();
        assertEquals(-43, calc.getValue());
    }

    @Test
    public void testJSObjectLiteralToNative() {
        JSObjectLiteralToNative obj = new JSObjectLiteralToNative();
        JSObjectLiteralToNativeClass obj2 = obj.returnLiteral();

        assertEquals("Hello", obj2.getPropA());
        assertEquals(102, obj2.getPropB());
    }

    @Test
    public void testFluentApiWithDerivedClasses() {
        // make sure that fluent API can be assigned to objects from derived classes
        DerivedFromAllTypes obj = new DerivedFromAllTypes();
        obj.setStringProperty("Hello");
        obj.setNumberProperty(12);
        assertEquals("Hello", obj.getStringProperty());
        assertEquals(12, obj.getNumberProperty());
    }

    /**
     * See that we can create a native object, pass it JS and then unmarshal
     * back without type information.
     */
    @Test
    public void creationOfNativeObjectsFromJavaScriptObjects() {
        AllTypes types = new AllTypes();

        Number jsObj = new Number(44);
        types.setAnyProperty(jsObj);
        Object unmarshalledJSObj = types.getAnyProperty();
        assertEquals(Number.class, unmarshalledJSObj.getClass());

        AddTen nativeObj = new AddTen(10);
        types.setAnyProperty(nativeObj);

        Object result1 = types.getAnyProperty();
        assertSame(nativeObj, result1);

        MulTen nativeObj2 = new MulTen(20);
        types.setAnyProperty(nativeObj2);
        Object unmarshalledNativeObj = types.getAnyProperty();
        assertEquals(MulTen.class, unmarshalledNativeObj.getClass());
        assertSame(nativeObj2, unmarshalledNativeObj);
    }

    @Test
    public void asyncOverrides_callAsyncMethod() {
        AsyncVirtualMethods obj = new AsyncVirtualMethods();
        assertEquals(128, obj.callMe());
        assertEquals(528, obj.overrideMe(44));
    }

    @Test
    public void asyncOverrides_overrideAsyncMethod() {
        OverrideAsyncMethods obj = new OverrideAsyncMethods();
        assertEquals(4452, obj.callMe());
    }

    @Test
    public void asyncOverrides_overrideAsyncMethodByParentClass() {
        OverrideAsyncMethodsByBaseClass obj = new OverrideAsyncMethodsByBaseClass();
        assertEquals(4452, obj.callMe());
    }

    @Test
    public void asyncOverrides_overrideCallsSuper() {
        OverrideCallsSuper obj = new OverrideCallsSuper();
        assertEquals(1441, obj.overrideMe(12));
        assertEquals(1209, obj.callMe());
    }

    @Test
    public void asyncOverrides_twoOverrides() {
        TwoOverrides obj = new TwoOverrides();
        assertEquals(684, obj.callMe());
    }

    @Test
    public void asyncOverrides_overrideThrows() {
        AsyncVirtualMethods obj = new AsyncVirtualMethods() {
            public java.lang.Number overrideMe(java.lang.Number mult) {
                throw new RuntimeException("Thrown by native code");
            }
        };

        boolean thrown = false;
        try {
            obj.callMe();
        } catch (JsiiException e) {
            assertTrue(e.getMessage().contains( "Thrown by native code"));
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void syncOverrides() {
        SyncOverrides obj = new SyncOverrides();
        assertEquals(10 * 5, obj.callerIsMethod());

        // affect the result
        obj.multiplier = 5;
        assertEquals(10 * 5 * 5, obj.callerIsMethod());

        // verify callbacks are invoked from a property
        assertEquals(10 * 5 * 5, obj.getCallerIsProperty());

        // and from an async method
        obj.multiplier = 3;
        assertEquals(10 * 5 * 3, obj.callerIsAsync());
    }

    /**
     * Allow overriding property getters and setters.
     */
    @Test
    public void propertyOverrides_get_set() {
        SyncOverrides so = new SyncOverrides();
        assertEquals("I am an override!", so.retrieveValueOfTheProperty());
        so.modifyValueOfTheProperty("New Value");
        assertEquals("New Value", so.anotherTheProperty);
    }

    @Test
    public void propertyOverrides_get_calls_super() {
        SyncVirtualMethods so = new SyncVirtualMethods() {
            public String getTheProperty() {
                String superValue = super.getTheProperty();
                return "super:" + superValue;
            }
        };

        assertEquals("super:initial value", so.retrieveValueOfTheProperty());
        assertEquals("super:initial value", so.getTheProperty());
    }

    @Test
    public void propertyOverrides_set_calls_super() {
        SyncVirtualMethods so = new SyncVirtualMethods() {
            @Override
            public void setTheProperty(String value) {
                super.setTheProperty(value + ":by override");
            }
        };

        so.modifyValueOfTheProperty("New Value");
        assertEquals("New Value:by override", so.getTheProperty());
    }

    @Test
    public void propertyOverrides_get_throws() {
        SyncVirtualMethods so = new SyncVirtualMethods() {
            public String getTheProperty() {
                throw new RuntimeException("Oh no, this is bad");
            }
        };

        boolean thrown = false;
        try {
            so.retrieveValueOfTheProperty();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Oh no, this is bad"));
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void propertyOverrides_set_throws() {
        SyncVirtualMethods so = new SyncVirtualMethods() {
            public void setTheProperty(String value) {
                throw new RuntimeException("Exception from overloaded setter");
            }
        };

        boolean thrown = false;
        try {
            so.modifyValueOfTheProperty("Hii");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Exception from overloaded setter"));
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void propertyOverrides_interfaces() {
        IInterfaceWithProperties obj = new IInterfaceWithProperties() {
            private String x;

            @Override
            public String getReadOnlyString() {
                return "READ_ONLY_STRING";
            }

            @Override
            public String getReadWriteString() {
                return x + "?";
            }

            @Override
            public void setReadWriteString(String value) {
                this.x = value + "!";
            }
        };

        UsesInterfaceWithProperties interact = new UsesInterfaceWithProperties(obj);
        assertEquals("READ_ONLY_STRING", interact.justRead());
        assertEquals("Hello!?", interact.writeAndRead("Hello"));
    }

    @Test
    public void interfaceBuilder() {
        IInterfaceWithProperties obj = IInterfaceWithProperties.builder()
                .withReadOnlyString("READ_ONLY")
                .withReadWriteString("READ_WRITE")
                .build();


        UsesInterfaceWithProperties interact = new UsesInterfaceWithProperties(obj);
        assertEquals("READ_ONLY", interact.justRead());
        assertEquals("Hello", interact.writeAndRead("Hello"));
    }

    @Test
    public void syncOverrides_callsSuper() {
        SyncOverrides obj = new SyncOverrides();
        assertEquals(10 * 5, obj.getCallerIsProperty());

        obj.returnSuper = true; // js code returns n * 2
        assertEquals(10 * 2, obj.getCallerIsProperty());
    }

    @Test(expected = JsiiException.class)
    public void fail_syncOverrides_callsDoubleAsync_method() {
        SyncOverrides obj = new SyncOverrides();
        obj.callAsync = true;

        obj.callerIsMethod();
    }

    @Test(expected = JsiiException.class)
    public void fail_syncOverrides_callsDoubleAsync_propertyGetter() {
        SyncOverrides obj = new SyncOverrides();
        obj.callAsync = true;

        obj.getCallerIsProperty();
    }

    @Test(expected = JsiiException.class)
    public void fail_syncOverrides_callsDoubleAsync_propertySetter() {
        SyncOverrides obj = new SyncOverrides();
        obj.callAsync = true;

        obj.setCallerIsProperty(12);
    }

    @Test
    public void testInterfaces() {
        IFriendly friendly;
        IFriendlier friendlier;
        IRandomNumberGenerator randomNumberGenerator;
        IFriendlyRandomGenerator friendlyRandomGenerator;

        Add add = new Add(new Number(10), new Number(20));
        friendly = add;
        // friendlier = add // <-- shouldn't compile since Add implements IFriendly
        assertEquals("Hello, I am a binary operation. What's your name?", friendly.hello());

        Multiply multiply = new Multiply(new Number(10), new Number(30));
        friendly = multiply;
        friendlier = multiply;
        randomNumberGenerator = multiply;
        // friendlyRandomGenerator = multiply; // <-- shouldn't compile
        assertEquals("Hello, I am a binary operation. What's your name?", friendly.hello());
        assertEquals("Goodbye from Multiply!", friendlier.goodbye());
        assertEquals(89, randomNumberGenerator.next());

        friendlyRandomGenerator = new DoubleTrouble();
        assertEquals("world", friendlyRandomGenerator.hello());
        assertEquals(12, friendlyRandomGenerator.next());

        Polymorphism poly = new Polymorphism();
        assertEquals("oh, Hello, I am a binary operation. What's your name?", poly.sayHello(friendly));
        assertEquals("oh, world", poly.sayHello(friendlyRandomGenerator));
        assertEquals("oh, SubclassNativeFriendlyRandom", poly.sayHello(new SubclassNativeFriendlyRandom()));
        assertEquals("oh, I am a native!", poly.sayHello(new PureNativeFriendlyRandom()));
    }

    /**
     * This test verifies that native objects passed to jsii code as interfaces will remain "stable"
     * across invocation. For native objects that derive from JsiiObject, that's natural, because the objref
     * is stored at the JsiiObject level. But for "pure" native objects, which are not part of the JsiiObject
     * hierarchy, there's some magic going on: when the pure object is first passed to jsii, an empty javascript
     * object is created for it (extends Object.prototype) and any native method overrides are assigned (like any
     * other jsii object). The resulting objref is stored at the engine level (in "objects").
     *
     * We verify two directions:
     * 1. objref => obj: when .getGenerator() is called, we get back an objref and we assert that it is the *same*
     *    as the one we originally passed.
     * 2. obj => objref: when we call .isSameGenerator(x) we pass the pure native object back to jsii and we expect
     *    that a new object is not created again.
     */
    @Test
    public void testNativeObjectsWithInterfaces() {
        // create a pure and native object, not part of the jsii hierarchy, only implements a jsii interface
        PureNativeFriendlyRandom pureNative = new PureNativeFriendlyRandom();
        SubclassNativeFriendlyRandom subclassedNative = new SubclassNativeFriendlyRandom();

        NumberGenerator generatorBoundToPSubclassedObject = new NumberGenerator(subclassedNative);
        assertSame(subclassedNative, generatorBoundToPSubclassedObject.getGenerator());
        generatorBoundToPSubclassedObject.isSameGenerator(subclassedNative);
        assertEquals(10000, generatorBoundToPSubclassedObject.nextTimes100());

        // when we invoke nextTimes100 again, it will use the objref and call into the same object.
        assertEquals(20000, generatorBoundToPSubclassedObject.nextTimes100());

        NumberGenerator generatorBoundToPureNative = new NumberGenerator(pureNative);
        assertSame(pureNative, generatorBoundToPureNative.getGenerator());
        generatorBoundToPureNative.isSameGenerator(pureNative);
        assertEquals(100000, generatorBoundToPureNative.nextTimes100());
        assertEquals(200000, generatorBoundToPureNative.nextTimes100());
    }

    @Test
    public void testLiteralInterface() {
        JSObjectLiteralForInterface obj = new JSObjectLiteralForInterface();
        IFriendly friendly = obj.giveMeFriendly();
        assertEquals("I am literally friendly!", friendly.hello());

        IFriendlyRandomGenerator gen = obj.giveMeFriendlyGenerator();
        assertEquals("giveMeFriendlyGenerator", gen.hello());
        assertEquals(42, gen.next());
    }

    @Test
    public void structs_stepBuilders() {
        Instant someInstant = Instant.now();
        DoubleTrouble nonPrim = new DoubleTrouble();

        DerivedStruct s = new DerivedStruct.Builder()
                .withNonPrimitive(nonPrim)
                .withBool(false)
                .withAnotherRequired(someInstant)
                .withAstring("Hello")
                .withAnumber(1234)
                .withFirstOptional(Arrays.asList("Hello", "World"))
                .build();

        assertSame(nonPrim, s.getNonPrimitive());
        assertEquals(false, s.getBool());
        assertEquals(someInstant, s.getAnotherRequired());
        assertEquals("Hello", s.getAstring());
        assertEquals(1234, s.getAnumber());
        assertEquals("World", s.getFirstOptional().get(1));
        assertNull(s.getAnotherOptional());
        assertNull(s.getOptionalArray());

        MyFirstStruct myFirstStruct = new MyFirstStruct.Builder()
                .withAstring("Hello")
                .withAnumber(12)
                .build();

        assertEquals("Hello", myFirstStruct.getAstring());
        assertEquals(12, myFirstStruct.getAnumber());

        StructWithOnlyOptionals onlyOptionals1 = new StructWithOnlyOptionals.Builder()
                .withOptional1("Hello")
                .withOptional2(1)
                .build();

        assertEquals("Hello", onlyOptionals1.getOptional1());
        assertEquals(1, onlyOptionals1.getOptional2());
        assertNull(onlyOptionals1.getOptional3());

        StructWithOnlyOptionals onlyOptionals2 = new StructWithOnlyOptionals.Builder().build();
        assertNull(onlyOptionals2.getOptional1());
        assertNull(onlyOptionals2.getOptional2());
        assertNull(onlyOptionals2.getOptional3());
    }

    @Test(expected = NullPointerException.class)
    public void structs_buildersContainNullChecks() {
        new MyFirstStruct.Builder().withAstring(null);
    }

    @Test
    public void structs_serializeToJsii() {
        MyFirstStruct firstStruct = MyFirstStruct.builder()
                .withAstring("FirstString")
                .withAnumber(999)
                .withFirstOptional(Arrays.asList("First", "Optional"))
                .build();

        DoubleTrouble doubleTrouble = new DoubleTrouble();

        DerivedStruct derivedStruct = DerivedStruct.builder()
                .withNonPrimitive(doubleTrouble)
                .withBool(false)
                .withAnotherRequired(Instant.now())
                .withAstring("String")
                .withAnumber(1234)
                .withFirstOptional(Arrays.asList("one", "two"))
                .build();

        GiveMeStructs gms = new GiveMeStructs();
        assertEquals(999, gms.readFirstNumber(firstStruct));
        assertEquals(1234, gms.readFirstNumber(derivedStruct)); // since derived inherits from first
        assertSame(doubleTrouble, gms.readDerivedNonPrimitive(derivedStruct));

        StructWithOnlyOptionals literal = gms.getStructLiteral();
        assertEquals("optional1FromStructLiteral", literal.getOptional1());
        assertEquals(false, literal.getOptional3());
        assertNull(literal.getOptional2());
    }

    @Test
    public void statics() {
        assertEquals("hello ,Yoyo!", Statics.staticMethod("Yoyo"));
        assertEquals("default", Statics.getInstance().getValue());

        Statics newStatics = new Statics("new value");
        Statics.setInstance(newStatics);
        assertSame(Statics.getInstance(), newStatics);
        assertEquals(Statics.getInstance().getValue(), "new value");

        assertEquals(100, Statics.getNonConstStatic());
    }

    @Test
    public void consts() {
        assertEquals("hello", Statics.FOO);
        DoubleTrouble obj = Statics.CONST_OBJ;
        assertEquals("world", obj.hello());
        assertEquals(1234, Statics.BAR);
        assertEquals("world", Statics.ZOO_BAR.get("hello"));
    }

    @Test
    public void reservedKeywordsAreSlugifiedInMethodNames() {
        JavaReservedWords obj = new JavaReservedWords();
        obj.import_();
        obj.const_();
        assertEquals("hello", obj.getWhile()); // properties do not need to be slufieid
    }

    @Test
    public void nodeStandardLibrary() {
        NodeStandardLibrary obj = new NodeStandardLibrary();
        assertEquals("Hello, resource!", obj.fsReadFile());
        assertEquals("Hello, resource! SYNC!", obj.fsReadFileSync());
        assertTrue(obj.getOsPlatform().length() > 0);
        assertEquals("6a2da20943931e9834fc12cfe5bb47bbd9ae43489a30726962b576f4e3993e50",
            obj.cryptoSha256());
    }

    @Test
    public void returnAbstract() {
        AbstractClassReturner obj = new AbstractClassReturner();
        AbstractClass obj2 = obj.giveMeAbstract();

        assertEquals("Hello, John!!", obj2.abstractMethod("John"));
        assertEquals("propFromInterfaceValue", obj2.getPropFromInterface());
        assertEquals(42, obj2.nonAbstractMethod());

        InterfaceImplementedByAbstractClass iface = obj.giveMeInterface();
        assertEquals("propFromInterfaceValue", iface.getPropFromInterface());

        assertEquals("hello-abstract-property", obj.getReturnAbstractFromProperty().getAbstractProperty());
    }

    @Test
    public void doNotOverridePrivates_method_public() {
        DoNotOverridePrivates obj = new DoNotOverridePrivates() {
            public String privateMethod() {
                return "privateMethod-Override";
            }
        };

        assertEquals("privateMethod", obj.privateMethodValue());
    }

    @Test
    public void doNotOverridePrivates_method_private() {
        DoNotOverridePrivates obj = new DoNotOverridePrivates() {
            private String privateMethod() {
                return "privateMethod-Override";
            }
        };

        assertEquals("privateMethod", obj.privateMethodValue());
    }

    @Test
    public void doNotOverridePrivates_property_by_name_private() {
        DoNotOverridePrivates obj = new DoNotOverridePrivates() {
            private String privateProperty() {
                return "privateProperty-Override";
            }
        };

        assertEquals("privateProperty", obj.privatePropertyValue());
    }

    @Test
    public void doNotOverridePrivates_property_by_name_public() {
        DoNotOverridePrivates obj = new DoNotOverridePrivates() {
            public String privateProperty() {
                return "privateProperty-Override";
            }
        };

        assertEquals("privateProperty", obj.privatePropertyValue());
    }

    @Test
    public void doNotOverridePrivates_property_getter_public() {
        DoNotOverridePrivates obj = new DoNotOverridePrivates() {
            public String getPrivateProperty() {
                return "privateProperty-Override";
            }
            public void setPrivateProperty(String value) {
                throw new RuntimeException("Boom");
            }
        };

        assertEquals("privateProperty", obj.privatePropertyValue());

        // verify the setter override is not invoked.
        obj.changePrivatePropertyValue("MyNewValue");
        assertEquals("MyNewValue", obj.privatePropertyValue());
    }

    @Test
    public void doNotOverridePrivates_property_getter_private() {
        DoNotOverridePrivates obj = new DoNotOverridePrivates() {
            private String getPrivateProperty() {
                return "privateProperty-Override";
            }
            public void setPrivateProperty(String value) {
                throw new RuntimeException("Boom");
            }
        };

        assertEquals("privateProperty", obj.privatePropertyValue());

        // verify the setter override is not invoked.
        obj.changePrivatePropertyValue("MyNewValue");
        assertEquals("MyNewValue", obj.privatePropertyValue());
    }

    @Test
    public void classWithPrivateConstructorAndAutomaticProperties() {
        ClassWithPrivateConstructorAndAutomaticProperties obj = ClassWithPrivateConstructorAndAutomaticProperties.create("Hello", "Bye");
        assertEquals("Bye", obj.getReadWriteString());
        obj.setReadWriteString("Hello");
        assertEquals("Hello", obj.getReadOnlyString());
    }

    static class MulTen extends Multiply {
        public MulTen(final int value) {
            super(new Number(value), new Number(10));
        }
    }

    static class AddTen extends Add {
        public AddTen(final int value) {
            super(new Number(value), new Number(10));
        }
    }

    static class DerivedFromAllTypes extends AllTypes {

    }

    static class OverrideAsyncMethods extends AsyncVirtualMethods {
        @Override
        public java.lang.Number overrideMe(java.lang.Number mult) {
            return this.foo() * 2;
        }

        /**
         * Implement another method, which doesn't override anything in the base class.
         * This should obviously be possible.
         */
        public int foo() {
            return 2222;
        }
    }

    static class OverrideAsyncMethodsByBaseClass extends OverrideAsyncMethods {

    }

    static class OverrideCallsSuper extends AsyncVirtualMethods {
        @Override
        public java.lang.Number overrideMe(java.lang.Number mult) {
            java.lang.Number superRet = super.overrideMe(mult);
            return superRet.intValue() * 10 + 1;
        }
    }

    static class TwoOverrides extends AsyncVirtualMethods {
        @Override
        public java.lang.Number overrideMe(java.lang.Number mult) {
            return 666;
        }

        @Override
        public java.lang.Number overrideMeToo() {
            return 10;
        }
    }

    static class SyncOverrides extends SyncVirtualMethods {

        int multiplier = 1;
        boolean returnSuper = false;
        boolean callAsync = false;

        @Override
        public java.lang.Number virtualMethod(java.lang.Number n) {
            if (returnSuper) {
                return super.virtualMethod(n);
            }

            if (callAsync) {
                OverrideAsyncMethods obj = new OverrideAsyncMethods();
                return obj.callMe();
            }

            return 5 * n.intValue() * multiplier;
        }

        @Override
        public String getTheProperty() {
            return "I am an override!";
        }

        @Override
        public void setTheProperty(String value) {
            this.anotherTheProperty = value;
        }

        /**
         * Used by the test that verifies that it is possible to override a property setter.
         */
        public String anotherTheProperty;
    }

    /**
     * In this case, the class does not derive from the JsiiObject hierarchy. It means
     * that when we pass it along to javascript, we won't have an objref. This should result
     * in creating a new empty javascript object and applying the overrides.
     *
     * The newly created objref will need to be stored somewhere (in the engine's object map)
     * so that subsequent calls won't create a new object every time.
     */
    class PureNativeFriendlyRandom implements IFriendlyRandomGenerator {

        private int nextNumber = 1000;

        @Override
        public java.lang.Number next() {
            int n = this.nextNumber;
            this.nextNumber += 1000;
            return n;
        }

        @Override
        public String hello() {
            return "I am a native!";
        }
    }

    class SubclassNativeFriendlyRandom extends Number implements IFriendly, IRandomNumberGenerator {

        private int nextNumber;

        public SubclassNativeFriendlyRandom() {
            super(908);
            this.nextNumber = 100;
        }

        @Override
        public String hello() {
            return "SubclassNativeFriendlyRandom";
        }

        @Override
        public java.lang.Number next() {
            int next = this.nextNumber;
            this.nextNumber += 100;
            return next;
        }
    }
}
