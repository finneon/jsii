package org.jsii.tests.calculator.composition;
/**
 * Abstract operation composed from an expression of other operations.
 */
@org.jsii.Jsii(module = org.jsii.tests.calculator.$Module.class, fqn = "jsii$jsii_calc$.composition.CompositeOperation")
public abstract class CompositeOperation extends org.jsii.tests.calculator.lib.Operation {
    protected CompositeOperation(final org.jsii.JsiiObject.InitializationMode mode) {
        super(mode);
    }
    /**
     * String representation of the value.
     */
    public java.lang.String toString() {
        return this.jsiiCall("toString", java.lang.String.class);
    }
    /**
     * The .toString() style.
     */
    public org.jsii.tests.calculator.composition.CompositionStringStyle getStringStyle() {
        return this.jsiiGet("stringStyle", org.jsii.tests.calculator.composition.CompositionStringStyle.class);
    }
    /**
     * The .toString() style.
     */
    public void setStringStyle(final org.jsii.tests.calculator.composition.CompositionStringStyle value) {
        this.jsiiSet("stringStyle", value);
    }
    /**
     * A set of prefixes to include in a decorated .toString().
     */
    public java.util.List<java.lang.String> getDecorationPrefixes() {
        return this.jsiiGet("decorationPrefixes", java.util.List.class);
    }
    /**
     * A set of prefixes to include in a decorated .toString().
     */
    public void setDecorationPrefixes(final java.util.List<java.lang.String> value) {
        this.jsiiSet("decorationPrefixes", value);
    }
    /**
     * A set of postfixes to include in a decorated .toString().
     */
    public java.util.List<java.lang.String> getDecorationPostfixes() {
        return this.jsiiGet("decorationPostfixes", java.util.List.class);
    }
    /**
     * A set of postfixes to include in a decorated .toString().
     */
    public void setDecorationPostfixes(final java.util.List<java.lang.String> value) {
        this.jsiiSet("decorationPostfixes", value);
    }
    /**
     * The value.
     */
    public java.lang.Number getValue() {
        return this.jsiiGet("value", java.lang.Number.class);
    }
    /**
     * The expression that this operation consists of.
     * Must be implemented by derived classes.
     */
    public org.jsii.tests.calculator.lib.Value getExpression() {
        return this.jsiiGet("expression", org.jsii.tests.calculator.lib.Value.class);
    }
}