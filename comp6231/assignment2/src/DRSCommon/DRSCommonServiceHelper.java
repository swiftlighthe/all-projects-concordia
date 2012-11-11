package DRSCommon;


/**
* DRSCommon/DRSCommonServiceHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from D:/workspace/comp6231ass2/src/DRSCommon.idl
* Saturday, October 27, 2012 6:12:47 PM EDT
*/

abstract public class DRSCommonServiceHelper
{
  private static String  _id = "IDL:DRSCommon/DRSCommonService:1.0";

  public static void insert (org.omg.CORBA.Any a, DRSCommon.DRSCommonService that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static DRSCommon.DRSCommonService extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (DRSCommon.DRSCommonServiceHelper.id (), "DRSCommonService");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static DRSCommon.DRSCommonService read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_DRSCommonServiceStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, DRSCommon.DRSCommonService value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static DRSCommon.DRSCommonService narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof DRSCommon.DRSCommonService)
      return (DRSCommon.DRSCommonService)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      DRSCommon._DRSCommonServiceStub stub = new DRSCommon._DRSCommonServiceStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static DRSCommon.DRSCommonService unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof DRSCommon.DRSCommonService)
      return (DRSCommon.DRSCommonService)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      DRSCommon._DRSCommonServiceStub stub = new DRSCommon._DRSCommonServiceStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}