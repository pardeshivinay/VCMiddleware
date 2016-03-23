package com.vc.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.vc.jpa.Bank;
import com.vc.jpa.Department;

@Path("/BankServices")
public class OTPServices {

	UserTransaction userTran;
	static {
		getEm();
	}

	public void init() {
		InitialContext ic;
		try {
			ic = new InitialContext();
			userTran = (UserTransaction) ic.lookup("java:comp/UserTransaction");
		} catch (NamingException e) {
			System.err.println("ERROR obtaining UserTransaction");
		}
	}

	// There are two ways of obtaining the connection information for some
	// services in Java

	// Method 1: Auto-configuration and JNDI
	// The Liberty buildpack automatically generates server.xml configuration
	// stanzas for the SQL Database service which contain the credentials needed
	// to
	// connect to the service. The buildpack generates a JNDI name following
	// the convention of "jdbc/<service_name>" where the <service_name> is the
	// name of the bound service.
	// Below we'll do a JNDI lookup for the EntityManager whose persistence
	// context is defined in web.xml. It references a persistence unit defined
	// in persistence.xml. In these XML files you'll see the
	// "jdbc/<service name>"
	// JNDI name used.

	// Method 2: Parsing VCAP_SERVICES environment variable (Not used)
	// The VCAP_SERVICES environment variable contains all the credentials of
	// services bound to this application. You can parse it to obtain the
	// information
	// needed to connect to the SQL Database service. SQL Database is a service
	// that the Liberty buildpack auto-configures as described above, so parsing
	// VCAP_SERVICES is not a best practice.

	private static EntityManager getEm() {
		InitialContext ic;
		try {
			ic = new InitialContext();
			return (EntityManager) ic
					.lookup("java:comp/env/bluemixboutiquepu/entitymanager");
		} catch (NamingException e) {
			System.out.println("ERROR obtaining EntityManager");
			e.printStackTrace();
		}
		return null;
	}

	@POST
	@Path("/Request")
	public Response create(InputStream incomingData) {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				incomingData));
		StringBuilder tabBankBuilder = new StringBuilder();
		String line = null;
		try {
			while ((line = in.readLine()) != null) {
				System.out.println("in=" + in + ",incomingData=" + incomingData
						+ ",in.readLine()=" + in.readLine());
				tabBankBuilder.append(line);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		init();

		EntityManager em = getEm();
		try {

			JSONParser jsonParser = new JSONParser();
			JSONObject json = (JSONObject) jsonParser.parse(tabBankBuilder
					.toString());
			System.out.println(json);
			Department department = new Department();
			department.setId(Integer.parseInt((String) json.get("id")));
			department.setManagerId(Integer.parseInt((String) json
					.get("managerId")));
			department.setName((String) json.get("name"));

			userTran.begin();
			em.persist(department);
			userTran.commit();
			return Response.ok(department.toString()).build();
		} catch (Exception e) {
			System.err.println("ERROR creating record");
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@POST
	@Path("/BankList")
	public Response getBankList(InputStream incomingData) {

		init();

		EntityManager em = getEm();
		try {

			List<Bank> list = em
					.createQuery("SELECT t FROM Bank t", Bank.class)
					.getResultList();
			JSONArray jsonArray = new JSONArray();
			if (list != null) {
				for (Bank bank : list) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", bank.getId());
					jsonObject.put("name", bank.getName());
					jsonArray.put(jsonObject);
				}
			}

			return Response.ok(jsonArray.toString()).build();
		} catch (Exception e) {
			System.err.println("ERROR creating record");
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	public static void main(String[] args) throws Exception {
		String tabBankBuilder = "{\"id\":\"1\",\"name\":\"admin\",\"managerId\":\"1\"}";
		// ObjectMapper mapper = new ObjectMapper();
		// Department department =(Department)mapper.readValue(tabBankBuilder,
		// Department.class);
		JSONParser jsonParser = new JSONParser();
		JSONObject department = (JSONObject) jsonParser.parse(tabBankBuilder);
		System.out.println(department.get("id"));
	}
}
