package server;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Properties;

import org.omg.PortableServer.IdAssignmentPolicyValue;
// import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.ThreadPolicyValue;

import common.SvrInfo;

public class DRSServer_Toronto {

	public static void main(String[] args) {

		Properties props = System.getProperties();
		props.setProperty("org.omg.CORBA.ORBClass",
				"com.sun.corba.se.internal.POA.POAORB");
		props.setProperty("org.omg.CORBA.ORBSingletonClass",
				"com.sun.corba.se.internal.corba.ORBSingleton");

		try {
			// Initialize the ORB.
			org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(args, props);

			// get a reference to the root POA
			org.omg.CORBA.Object obj = orb
					.resolve_initial_references("RootPOA");
			POA poaRoot = POAHelper.narrow(obj);

			// Create policies for our persistent POA
			org.omg.CORBA.Policy[] policies = {
					// poaRoot.create_lifespan_policy(LifespanPolicyValue.PERSISTENT),
					poaRoot.create_id_assignment_policy(IdAssignmentPolicyValue.USER_ID),
					poaRoot.create_thread_policy(ThreadPolicyValue.ORB_CTRL_MODEL) };

			// Create myPOA with the right policies
			POA poa = poaRoot.create_POA("DRSCommonServiceServerImpl_poa",
					poaRoot.the_POAManager(), policies);

			// Create the servant
			DRSCommonServiceServerImpl servant = new DRSCommonServiceServerImpl(
					SvrInfo.SVR_NAME_TORONTO);

			// Activate the servant with the ID on myPOA
			byte[] objectId = servant.svrName.getBytes();
			poa.activate_object_with_id(objectId, servant);

			// Activate the POA manager
			poaRoot.the_POAManager().activate();

			// Get a reference to the servant and write it down.
			obj = poa.servant_to_reference(servant);

			// ---- Uncomment below to enable Naming Service access. ----
//			org.omg.CORBA.Object ncobj = orb
//					.resolve_initial_references("NameService");
//			NamingContextExt nc = NamingContextExtHelper.narrow(ncobj);
//			nc.bind(nc.to_name(servant.svrName), obj);

			PrintWriter ps = new PrintWriter(
					new FileOutputStream(new File(servant.svrName)));
			ps.println(orb.object_to_string(obj));
			ps.close();

			System.out.println(servant.svrName + " is ready...");

			// Wait for incoming requests
			orb.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
