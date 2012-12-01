package ca.com.Concordia.distributed.Ass_2.MyServer;


/**
* MyServer/MyServicePOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from D:/work/DRS_CORBA/simple.idl
* Thursday, November 29, 2012 4:20:37 o'clock PM CST
*/

public abstract class MyServicePOA extends org.omg.PortableServer.Servant
 implements ca.com.Concordia.distributed.Ass_2.MyServer.MyServiceOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("purchase", new java.lang.Integer (0));
    _methods.put ("giveback", new java.lang.Integer (1));
    _methods.put ("checkStock", new java.lang.Integer (2));
    _methods.put ("haveThisItem", new java.lang.Integer (3));
    _methods.put ("exChange", new java.lang.Integer (4));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // MyServer/MyService/purchase
       {
         int customerID = in.read_long ();
         String itemID = in.read_string ();
         int numberOfItem = in.read_long ();
         String $result = null;
         $result = this.purchase (customerID, itemID, numberOfItem);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // MyServer/MyService/giveback
       {
         int customerID = in.read_long ();
         String itemID = in.read_string ();
         int numberOfItem = in.read_long ();
         String $result = null;
         $result = this.giveback (customerID, itemID, numberOfItem);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 2:  // MyServer/MyService/checkStock
       {
         String itemID = in.read_string ();
         int $result = (int)0;
         $result = this.checkStock (itemID);
         out = $rh.createReply();
         out.write_long ($result);
         break;
       }

       case 3:  // MyServer/MyService/haveThisItem
       {
         String itemID = in.read_string ();
         boolean $result = false;
         $result = this.haveThisItem (itemID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 4:  // MyServer/MyService/exChange
       {
         String itemID1 = in.read_string ();
         String itemID2 = in.read_string ();
         int customerID = in.read_long ();
         int numberOfItem = in.read_long ();
         String $result = null;
         $result = this.exChange (itemID1, itemID2, customerID, numberOfItem);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:MyServer/MyService:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public MyService _this() 
  {
    return MyServiceHelper.narrow(
    super._this_object());
  }

  public MyService _this(org.omg.CORBA.ORB orb) 
  {
    return MyServiceHelper.narrow(
    super._this_object(orb));
  }


} // class MyServicePOA
