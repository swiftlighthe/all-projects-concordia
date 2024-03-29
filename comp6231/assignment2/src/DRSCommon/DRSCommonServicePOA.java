package DRSCommon;


/**
* DRSCommon/DRSCommonServicePOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from D:/workspace/comp6231ass2/src/DRSCommon.idl
* Saturday, October 27, 2012 6:12:47 PM EDT
*/

public abstract class DRSCommonServicePOA extends org.omg.PortableServer.Servant
 implements DRSCommon.DRSCommonServiceOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("buy", new java.lang.Integer (0));
    _methods.put ("returnNumOfItem", new java.lang.Integer (1));
    _methods.put ("checkStock", new java.lang.Integer (2));
    _methods.put ("exchange", new java.lang.Integer (3));
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
       case 0:  // DRSCommon/DRSCommonService/buy
       {
         String customerID = in.read_string ();
         String itemID = in.read_string ();
         int numberOfItem = in.read_long ();
         int $result = (int)0;
         $result = this.buy (customerID, itemID, numberOfItem);
         out = $rh.createReply();
         out.write_long ($result);
         break;
       }

       case 1:  // DRSCommon/DRSCommonService/returnNumOfItem
       {
         String customerID = in.read_string ();
         String itemID = in.read_string ();
         int numberOfItem = in.read_long ();
         int $result = (int)0;
         $result = this.returnNumOfItem (customerID, itemID, numberOfItem);
         out = $rh.createReply();
         out.write_long ($result);
         break;
       }

       case 2:  // DRSCommon/DRSCommonService/checkStock
       {
         String itemID = in.read_string ();
         String $result = null;
         $result = this.checkStock (itemID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 3:  // DRSCommon/DRSCommonService/exchange
       {
         String customerID = in.read_string ();
         String boughtItemID = in.read_string ();
         int boughtNumber = in.read_long ();
         String desiredItemID = in.read_string ();
         int desiredNumber = in.read_long ();
         int $result = (int)0;
         $result = this.exchange (customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
         out = $rh.createReply();
         out.write_long ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:DRSCommon/DRSCommonService:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public DRSCommonService _this() 
  {
    return DRSCommonServiceHelper.narrow(
    super._this_object());
  }

  public DRSCommonService _this(org.omg.CORBA.ORB orb) 
  {
    return DRSCommonServiceHelper.narrow(
    super._this_object(orb));
  }


} // class DRSCommonServicePOA
