package com.vc.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.vc.jpa.Bank;
import com.vc.jpa.Customer;
import com.vc.jpa.VirtualAddress;
import com.vc.jpa.VirtualCardMaster;

@Path("/UPIServices")
public class UPIServices {

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
	@Path("/VirtualAddressList")
	public Response getVirtualAddressList(InputStream incomingData) {

		init();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				incomingData));
		StringBuilder tabBankBuilder = new StringBuilder();
		EntityManager em = getEm();
		String customerId = "";
		String line = null;
		try {
			while ((line = in.readLine()) != null) {
				System.out.println("in=" + in + ",incomingData=" + incomingData
						+ ",in.readLine()=" + in.readLine());
				tabBankBuilder.append(line);
			}

			JSONParser jsonParser = new JSONParser();
			JSONObject json = (JSONObject) jsonParser.parse(tabBankBuilder
					.toString());
			customerId = (String) json.get("customerId");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (customerId != null && !"".equals(customerId)) {
			try {
				Query query = em
						.createNativeQuery("SELECT t.id,t.VIRTUAL_ADDRESS,t.ACCOUNTNO,m.ID,m.card_no,m.isActive,d.name,d.STATUS,r.id,"
								+ "	r.PARAMKEY,r.PARAMVAL FROM customer_Virtual_Address t"
								+ " left outer join VIRTUAL_CARD_MASTER m on m.VIRTUAL_ADDRESS=t.VIRTUAL_ADDRESS "
								+ " left outer join VIRTUAL_CARD_PERSONAL_DETAIL d on d.CARDID=m.ID "
								+ " left outer join VIRTUAL_CARD_RESTRICTIONS r on r.CARDID=m.ID "
								+ " where t.customerId="+customerId);
				

				List resultat = query.getResultList();
				System.out.println("resultat  =" + resultat);
				JSONArray jsonArray = new JSONArray();
				if (resultat != null) {
					for (Iterator i = resultat.iterator(); i.hasNext();) {
						Object[] values = (Object[]) i.next();
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("customerId", customerId);
						jsonObject.put("virtualAddress", values[1]);
						jsonObject.put("accountNo", values[2]);
						jsonObject.put("cardNo", values[4]);
						jsonObject.put("isActive", values[5]);
						jsonObject.put("name", values[6]);
						jsonObject.put("status", values[7]);
						jsonObject.put("restrictionId", values[8]);
						jsonObject.put("restrictionType", values[9]);
						jsonObject.put("restrictionValue", values[10]);
						jsonArray.put(jsonObject);

					}
					return Response.ok(jsonArray.toString()).build();
				}
				/*
				 * List<VirtualAddress> addressList = query.getResultList();
				 * JSONArray jsonArray = new JSONArray(); if (addressList !=
				 * null && !addressList.isEmpty()) { for (VirtualAddress
				 * virtualAddress : addressList) { JSONObject jsonObject = new
				 * JSONObject(); jsonObject.put("customerId",
				 * virtualAddress.getCustomerId()); jsonObject.put("id",
				 * virtualAddress.getId()); jsonObject.put("accountNo",
				 * virtualAddress.getAccountNo());
				 * jsonObject.put("virtualAddress",
				 * virtualAddress.getVirtualAddress());
				 * //jsonObject.put("virtualCardNo",
				 * virtualAddress.getVirtualAddress());
				 * //jsonObject.put("virtualCardId",
				 * virtualAddress.getVirtualAddress());
				 * //jsonObject.put("virtualCardHolder",
				 * virtualAddress.getVirtualAddress());
				 * jsonArray.put(jsonObject); }
				 * System.out.println("jsonArray.toString() =="
				 * +jsonArray.toString()); return
				 * Response.ok(jsonArray.toString()).build(); }
				 */else {
					return Response
							.ok("{\"Result\":\"No virtual addresses found\",\"resultCode\":\"00\"}")
							.build();
				}
			} catch (Exception e) {
				System.err.println("ERROR creating record-" + e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			return Response.ok("Customer Id cannot be empty").build();

		}
	}

	@POST
	@Path("/VirtualAddressDetails")
	public Response getVirtualAddressDetails(InputStream incomingData) {

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

	@POST
	@Path("/CreateVirtualAddress")
	public Response createVirtualAddress(InputStream incomingData) {
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
			VirtualAddress virtualAddress = new VirtualAddress();
			virtualAddress.setAccountNo((String) json.get("accountNo"));
			virtualAddress.setCreatedTime(new Timestamp(System
					.currentTimeMillis()));
			virtualAddress.setCustomerId((String) json.get("customerId"));
			virtualAddress.setVirtualAddress((String) json
					.get("virtualAddress"));
			userTran.begin();
			em.persist(virtualAddress);
			userTran.commit();
			return Response.ok(virtualAddress.toString()).build();
		} catch (Exception e) {
			System.err.println("ERROR creating record-" + e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@POST
	@Path("/ValidateVirtualAddress")
	public Response validateVirtualAddress(InputStream incomingData) {
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
			String virtualAddress = (String) json.get("virtualAddress");

			Query query = em
					.createQuery(
							"SELECT t FROM VirtualAddress t where t.virtualAddress=:virtualAddress",
							VirtualAddress.class);
			query.setParameter("virtualAddress", virtualAddress);
			List<VirtualAddress> list = query.getResultList();
			if (list != null && !list.isEmpty()) {
				return Response
						.ok("{\"Result\":\"Virtual Address exists\",\"resultCode\":\"01\"}")
						.build();
			} else {
				return Response
						.ok("{\"Result\":\"Virtual Address does not exist\",\"resultCode\":\"01\"}")
						.build();
			}
		} catch (Exception e) {
			System.err.println("ERROR creating record-" + e);
			return Response
					.ok("{\"Result\":\"Internal Server Error\",\"resultCode\":\"00\"}")
					.build();
		}

	}

}
