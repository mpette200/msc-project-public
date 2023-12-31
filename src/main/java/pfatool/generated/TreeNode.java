/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package pfatool.generated;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class TreeNode extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -235588043459153934L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"TreeNode\",\"namespace\":\"pfatool.generated\",\"fields\":[{\"name\":\"field\",\"type\":{\"type\":\"enum\",\"name\":\"ColumnNames\",\"symbols\":[\"lag_00\",\"lag_01\",\"lag_02\",\"lag_03\",\"lag_04\",\"lag_05\",\"lag_06\",\"lag_07\",\"lag_08\",\"lag_09\",\"lag_10\",\"lag_11\",\"lag_12\",\"lag_13\",\"lag_14\",\"lag_15\",\"lag_16\",\"lag_17\",\"lag_18\",\"lag_19\",\"lag_20\",\"lag_21\",\"lag_22\",\"lag_23\",\"lag_24\",\"lag_25\",\"lag_26\",\"lag_27\",\"lag_28\",\"lag_29\",\"lag_30\",\"lag_31\",\"lag_32\",\"lag_33\",\"lag_34\",\"lag_35\",\"lag_36\",\"lag_37\",\"lag_38\",\"lag_39\",\"lag_40\",\"lag_41\"]}},{\"name\":\"operator\",\"type\":\"string\"},{\"name\":\"value\",\"type\":\"double\"},{\"name\":\"pass\",\"type\":[\"TreeNode\",\"double\"]},{\"name\":\"fail\",\"type\":[\"TreeNode\",\"double\"]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<TreeNode> ENCODER =
      new BinaryMessageEncoder<TreeNode>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<TreeNode> DECODER =
      new BinaryMessageDecoder<TreeNode>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<TreeNode> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<TreeNode> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<TreeNode> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<TreeNode>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this TreeNode to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a TreeNode from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a TreeNode instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static TreeNode fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private pfatool.generated.ColumnNames field;
   private java.lang.CharSequence operator;
   private double value;
   private java.lang.Object pass;
   private java.lang.Object fail;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public TreeNode() {}

  /**
   * All-args constructor.
   * @param field The new value for field
   * @param operator The new value for operator
   * @param value The new value for value
   * @param pass The new value for pass
   * @param fail The new value for fail
   */
  public TreeNode(pfatool.generated.ColumnNames field, java.lang.CharSequence operator, java.lang.Double value, java.lang.Object pass, java.lang.Object fail) {
    this.field = field;
    this.operator = operator;
    this.value = value;
    this.pass = pass;
    this.fail = fail;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return field;
    case 1: return operator;
    case 2: return value;
    case 3: return pass;
    case 4: return fail;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: field = (pfatool.generated.ColumnNames)value$; break;
    case 1: operator = (java.lang.CharSequence)value$; break;
    case 2: value = (java.lang.Double)value$; break;
    case 3: pass = value$; break;
    case 4: fail = value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'field' field.
   * @return The value of the 'field' field.
   */
  public pfatool.generated.ColumnNames getField() {
    return field;
  }


  /**
   * Sets the value of the 'field' field.
   * @param value the value to set.
   */
  public void setField(pfatool.generated.ColumnNames value) {
    this.field = value;
  }

  /**
   * Gets the value of the 'operator' field.
   * @return The value of the 'operator' field.
   */
  public java.lang.CharSequence getOperator() {
    return operator;
  }


  /**
   * Sets the value of the 'operator' field.
   * @param value the value to set.
   */
  public void setOperator(java.lang.CharSequence value) {
    this.operator = value;
  }

  /**
   * Gets the value of the 'value' field.
   * @return The value of the 'value' field.
   */
  public double getValue() {
    return value;
  }


  /**
   * Sets the value of the 'value' field.
   * @param value the value to set.
   */
  public void setValue(double value) {
    this.value = value;
  }

  /**
   * Gets the value of the 'pass' field.
   * @return The value of the 'pass' field.
   */
  public java.lang.Object getPass() {
    return pass;
  }


  /**
   * Sets the value of the 'pass' field.
   * @param value the value to set.
   */
  public void setPass(java.lang.Object value) {
    this.pass = value;
  }

  /**
   * Gets the value of the 'fail' field.
   * @return The value of the 'fail' field.
   */
  public java.lang.Object getFail() {
    return fail;
  }


  /**
   * Sets the value of the 'fail' field.
   * @param value the value to set.
   */
  public void setFail(java.lang.Object value) {
    this.fail = value;
  }

  /**
   * Creates a new TreeNode RecordBuilder.
   * @return A new TreeNode RecordBuilder
   */
  public static pfatool.generated.TreeNode.Builder newBuilder() {
    return new pfatool.generated.TreeNode.Builder();
  }

  /**
   * Creates a new TreeNode RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new TreeNode RecordBuilder
   */
  public static pfatool.generated.TreeNode.Builder newBuilder(pfatool.generated.TreeNode.Builder other) {
    if (other == null) {
      return new pfatool.generated.TreeNode.Builder();
    } else {
      return new pfatool.generated.TreeNode.Builder(other);
    }
  }

  /**
   * Creates a new TreeNode RecordBuilder by copying an existing TreeNode instance.
   * @param other The existing instance to copy.
   * @return A new TreeNode RecordBuilder
   */
  public static pfatool.generated.TreeNode.Builder newBuilder(pfatool.generated.TreeNode other) {
    if (other == null) {
      return new pfatool.generated.TreeNode.Builder();
    } else {
      return new pfatool.generated.TreeNode.Builder(other);
    }
  }

  /**
   * RecordBuilder for TreeNode instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<TreeNode>
    implements org.apache.avro.data.RecordBuilder<TreeNode> {

    private pfatool.generated.ColumnNames field;
    private java.lang.CharSequence operator;
    private double value;
    private java.lang.Object pass;
    private java.lang.Object fail;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(pfatool.generated.TreeNode.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.field)) {
        this.field = data().deepCopy(fields()[0].schema(), other.field);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.operator)) {
        this.operator = data().deepCopy(fields()[1].schema(), other.operator);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.value)) {
        this.value = data().deepCopy(fields()[2].schema(), other.value);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.pass)) {
        this.pass = data().deepCopy(fields()[3].schema(), other.pass);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.fail)) {
        this.fail = data().deepCopy(fields()[4].schema(), other.fail);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
    }

    /**
     * Creates a Builder by copying an existing TreeNode instance
     * @param other The existing instance to copy.
     */
    private Builder(pfatool.generated.TreeNode other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.field)) {
        this.field = data().deepCopy(fields()[0].schema(), other.field);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.operator)) {
        this.operator = data().deepCopy(fields()[1].schema(), other.operator);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.value)) {
        this.value = data().deepCopy(fields()[2].schema(), other.value);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.pass)) {
        this.pass = data().deepCopy(fields()[3].schema(), other.pass);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.fail)) {
        this.fail = data().deepCopy(fields()[4].schema(), other.fail);
        fieldSetFlags()[4] = true;
      }
    }

    /**
      * Gets the value of the 'field' field.
      * @return The value.
      */
    public pfatool.generated.ColumnNames getField() {
      return field;
    }


    /**
      * Sets the value of the 'field' field.
      * @param value The value of 'field'.
      * @return This builder.
      */
    public pfatool.generated.TreeNode.Builder setField(pfatool.generated.ColumnNames value) {
      validate(fields()[0], value);
      this.field = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'field' field has been set.
      * @return True if the 'field' field has been set, false otherwise.
      */
    public boolean hasField() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'field' field.
      * @return This builder.
      */
    public pfatool.generated.TreeNode.Builder clearField() {
      field = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'operator' field.
      * @return The value.
      */
    public java.lang.CharSequence getOperator() {
      return operator;
    }


    /**
      * Sets the value of the 'operator' field.
      * @param value The value of 'operator'.
      * @return This builder.
      */
    public pfatool.generated.TreeNode.Builder setOperator(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.operator = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'operator' field has been set.
      * @return True if the 'operator' field has been set, false otherwise.
      */
    public boolean hasOperator() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'operator' field.
      * @return This builder.
      */
    public pfatool.generated.TreeNode.Builder clearOperator() {
      operator = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'value' field.
      * @return The value.
      */
    public double getValue() {
      return value;
    }


    /**
      * Sets the value of the 'value' field.
      * @param value The value of 'value'.
      * @return This builder.
      */
    public pfatool.generated.TreeNode.Builder setValue(double value) {
      validate(fields()[2], value);
      this.value = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'value' field has been set.
      * @return True if the 'value' field has been set, false otherwise.
      */
    public boolean hasValue() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'value' field.
      * @return This builder.
      */
    public pfatool.generated.TreeNode.Builder clearValue() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'pass' field.
      * @return The value.
      */
    public java.lang.Object getPass() {
      return pass;
    }


    /**
      * Sets the value of the 'pass' field.
      * @param value The value of 'pass'.
      * @return This builder.
      */
    public pfatool.generated.TreeNode.Builder setPass(java.lang.Object value) {
      validate(fields()[3], value);
      this.pass = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'pass' field has been set.
      * @return True if the 'pass' field has been set, false otherwise.
      */
    public boolean hasPass() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'pass' field.
      * @return This builder.
      */
    public pfatool.generated.TreeNode.Builder clearPass() {
      pass = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'fail' field.
      * @return The value.
      */
    public java.lang.Object getFail() {
      return fail;
    }


    /**
      * Sets the value of the 'fail' field.
      * @param value The value of 'fail'.
      * @return This builder.
      */
    public pfatool.generated.TreeNode.Builder setFail(java.lang.Object value) {
      validate(fields()[4], value);
      this.fail = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'fail' field has been set.
      * @return True if the 'fail' field has been set, false otherwise.
      */
    public boolean hasFail() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'fail' field.
      * @return This builder.
      */
    public pfatool.generated.TreeNode.Builder clearFail() {
      fail = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TreeNode build() {
      try {
        TreeNode record = new TreeNode();
        record.field = fieldSetFlags()[0] ? this.field : (pfatool.generated.ColumnNames) defaultValue(fields()[0]);
        record.operator = fieldSetFlags()[1] ? this.operator : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.value = fieldSetFlags()[2] ? this.value : (java.lang.Double) defaultValue(fields()[2]);
        record.pass = fieldSetFlags()[3] ? this.pass :  defaultValue(fields()[3]);
        record.fail = fieldSetFlags()[4] ? this.fail :  defaultValue(fields()[4]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<TreeNode>
    WRITER$ = (org.apache.avro.io.DatumWriter<TreeNode>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<TreeNode>
    READER$ = (org.apache.avro.io.DatumReader<TreeNode>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}










