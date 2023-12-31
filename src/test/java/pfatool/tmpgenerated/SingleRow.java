/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package pfatool.tmpgenerated;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class SingleRow extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -6485958926762297557L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"SingleRow\",\"namespace\":\"pfatool.tmpgenerated\",\"fields\":[{\"name\":\"x0\",\"type\":\"double\"},{\"name\":\"x1\",\"type\":\"double\"},{\"name\":\"x2\",\"type\":\"double\"},{\"name\":\"x3\",\"type\":\"double\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<SingleRow> ENCODER =
      new BinaryMessageEncoder<SingleRow>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<SingleRow> DECODER =
      new BinaryMessageDecoder<SingleRow>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<SingleRow> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<SingleRow> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<SingleRow> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<SingleRow>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this SingleRow to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a SingleRow from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a SingleRow instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static SingleRow fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private double x0;
   private double x1;
   private double x2;
   private double x3;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public SingleRow() {}

  /**
   * All-args constructor.
   * @param x0 The new value for x0
   * @param x1 The new value for x1
   * @param x2 The new value for x2
   * @param x3 The new value for x3
   */
  public SingleRow(java.lang.Double x0, java.lang.Double x1, java.lang.Double x2, java.lang.Double x3) {
    this.x0 = x0;
    this.x1 = x1;
    this.x2 = x2;
    this.x3 = x3;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return x0;
    case 1: return x1;
    case 2: return x2;
    case 3: return x3;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: x0 = (java.lang.Double)value$; break;
    case 1: x1 = (java.lang.Double)value$; break;
    case 2: x2 = (java.lang.Double)value$; break;
    case 3: x3 = (java.lang.Double)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'x0' field.
   * @return The value of the 'x0' field.
   */
  public double getX0() {
    return x0;
  }


  /**
   * Sets the value of the 'x0' field.
   * @param value the value to set.
   */
  public void setX0(double value) {
    this.x0 = value;
  }

  /**
   * Gets the value of the 'x1' field.
   * @return The value of the 'x1' field.
   */
  public double getX1() {
    return x1;
  }


  /**
   * Sets the value of the 'x1' field.
   * @param value the value to set.
   */
  public void setX1(double value) {
    this.x1 = value;
  }

  /**
   * Gets the value of the 'x2' field.
   * @return The value of the 'x2' field.
   */
  public double getX2() {
    return x2;
  }


  /**
   * Sets the value of the 'x2' field.
   * @param value the value to set.
   */
  public void setX2(double value) {
    this.x2 = value;
  }

  /**
   * Gets the value of the 'x3' field.
   * @return The value of the 'x3' field.
   */
  public double getX3() {
    return x3;
  }


  /**
   * Sets the value of the 'x3' field.
   * @param value the value to set.
   */
  public void setX3(double value) {
    this.x3 = value;
  }

  /**
   * Creates a new SingleRow RecordBuilder.
   * @return A new SingleRow RecordBuilder
   */
  public static pfatool.tmpgenerated.SingleRow.Builder newBuilder() {
    return new pfatool.tmpgenerated.SingleRow.Builder();
  }

  /**
   * Creates a new SingleRow RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new SingleRow RecordBuilder
   */
  public static pfatool.tmpgenerated.SingleRow.Builder newBuilder(pfatool.tmpgenerated.SingleRow.Builder other) {
    if (other == null) {
      return new pfatool.tmpgenerated.SingleRow.Builder();
    } else {
      return new pfatool.tmpgenerated.SingleRow.Builder(other);
    }
  }

  /**
   * Creates a new SingleRow RecordBuilder by copying an existing SingleRow instance.
   * @param other The existing instance to copy.
   * @return A new SingleRow RecordBuilder
   */
  public static pfatool.tmpgenerated.SingleRow.Builder newBuilder(pfatool.tmpgenerated.SingleRow other) {
    if (other == null) {
      return new pfatool.tmpgenerated.SingleRow.Builder();
    } else {
      return new pfatool.tmpgenerated.SingleRow.Builder(other);
    }
  }

  /**
   * RecordBuilder for SingleRow instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<SingleRow>
    implements org.apache.avro.data.RecordBuilder<SingleRow> {

    private double x0;
    private double x1;
    private double x2;
    private double x3;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(pfatool.tmpgenerated.SingleRow.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.x0)) {
        this.x0 = data().deepCopy(fields()[0].schema(), other.x0);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.x1)) {
        this.x1 = data().deepCopy(fields()[1].schema(), other.x1);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.x2)) {
        this.x2 = data().deepCopy(fields()[2].schema(), other.x2);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.x3)) {
        this.x3 = data().deepCopy(fields()[3].schema(), other.x3);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
    }

    /**
     * Creates a Builder by copying an existing SingleRow instance
     * @param other The existing instance to copy.
     */
    private Builder(pfatool.tmpgenerated.SingleRow other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.x0)) {
        this.x0 = data().deepCopy(fields()[0].schema(), other.x0);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.x1)) {
        this.x1 = data().deepCopy(fields()[1].schema(), other.x1);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.x2)) {
        this.x2 = data().deepCopy(fields()[2].schema(), other.x2);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.x3)) {
        this.x3 = data().deepCopy(fields()[3].schema(), other.x3);
        fieldSetFlags()[3] = true;
      }
    }

    /**
      * Gets the value of the 'x0' field.
      * @return The value.
      */
    public double getX0() {
      return x0;
    }


    /**
      * Sets the value of the 'x0' field.
      * @param value The value of 'x0'.
      * @return This builder.
      */
    public pfatool.tmpgenerated.SingleRow.Builder setX0(double value) {
      validate(fields()[0], value);
      this.x0 = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'x0' field has been set.
      * @return True if the 'x0' field has been set, false otherwise.
      */
    public boolean hasX0() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'x0' field.
      * @return This builder.
      */
    public pfatool.tmpgenerated.SingleRow.Builder clearX0() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'x1' field.
      * @return The value.
      */
    public double getX1() {
      return x1;
    }


    /**
      * Sets the value of the 'x1' field.
      * @param value The value of 'x1'.
      * @return This builder.
      */
    public pfatool.tmpgenerated.SingleRow.Builder setX1(double value) {
      validate(fields()[1], value);
      this.x1 = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'x1' field has been set.
      * @return True if the 'x1' field has been set, false otherwise.
      */
    public boolean hasX1() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'x1' field.
      * @return This builder.
      */
    public pfatool.tmpgenerated.SingleRow.Builder clearX1() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'x2' field.
      * @return The value.
      */
    public double getX2() {
      return x2;
    }


    /**
      * Sets the value of the 'x2' field.
      * @param value The value of 'x2'.
      * @return This builder.
      */
    public pfatool.tmpgenerated.SingleRow.Builder setX2(double value) {
      validate(fields()[2], value);
      this.x2 = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'x2' field has been set.
      * @return True if the 'x2' field has been set, false otherwise.
      */
    public boolean hasX2() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'x2' field.
      * @return This builder.
      */
    public pfatool.tmpgenerated.SingleRow.Builder clearX2() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'x3' field.
      * @return The value.
      */
    public double getX3() {
      return x3;
    }


    /**
      * Sets the value of the 'x3' field.
      * @param value The value of 'x3'.
      * @return This builder.
      */
    public pfatool.tmpgenerated.SingleRow.Builder setX3(double value) {
      validate(fields()[3], value);
      this.x3 = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'x3' field has been set.
      * @return True if the 'x3' field has been set, false otherwise.
      */
    public boolean hasX3() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'x3' field.
      * @return This builder.
      */
    public pfatool.tmpgenerated.SingleRow.Builder clearX3() {
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SingleRow build() {
      try {
        SingleRow record = new SingleRow();
        record.x0 = fieldSetFlags()[0] ? this.x0 : (java.lang.Double) defaultValue(fields()[0]);
        record.x1 = fieldSetFlags()[1] ? this.x1 : (java.lang.Double) defaultValue(fields()[1]);
        record.x2 = fieldSetFlags()[2] ? this.x2 : (java.lang.Double) defaultValue(fields()[2]);
        record.x3 = fieldSetFlags()[3] ? this.x3 : (java.lang.Double) defaultValue(fields()[3]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<SingleRow>
    WRITER$ = (org.apache.avro.io.DatumWriter<SingleRow>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<SingleRow>
    READER$ = (org.apache.avro.io.DatumReader<SingleRow>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeDouble(this.x0);

    out.writeDouble(this.x1);

    out.writeDouble(this.x2);

    out.writeDouble(this.x3);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.x0 = in.readDouble();

      this.x1 = in.readDouble();

      this.x2 = in.readDouble();

      this.x3 = in.readDouble();

    } else {
      for (int i = 0; i < 4; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.x0 = in.readDouble();
          break;

        case 1:
          this.x1 = in.readDouble();
          break;

        case 2:
          this.x2 = in.readDouble();
          break;

        case 3:
          this.x3 = in.readDouble();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










