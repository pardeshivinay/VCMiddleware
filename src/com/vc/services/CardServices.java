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
import com.vc.jpa.LimitMaster;
import com.vc.jpa.VirtualCardMaster;
import com.vc.jpa.VirtualCardPersonalDetails;
import com.vc.jpa.VirtualCardRestrictions;
import com.vc.util.RandomOtp;

@Path("/CardServices")
public class CardServices {

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
	@Path("/VirtualCardList")
	public Response getVirtualCardList(InputStream incomingData) {

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

		
		try {
		/*	Query query = em
					.createNativeQuery("SELECT t.id,t.VIRTUAL_ADDRESS,t.ACCOUNTNO,m.ID,m.card_no,"
							+ " m.isActive,d.name,d.STATUS "
							+ " FROM customer_Virtual_Address t left outer join "
							+ " VIRTUAL_CARD_MASTER m on m.VIRTUAL_ADDRESS=t.VIRTUAL_ADDRESS "
							+ " left outer join VIRTUAL_CARD_PERSONAL_DETAIL d on d.CARDID=m.ID  "
							+ " where t.customerId="+customerId);*/
			
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

			return Response.ok(jsonArray.toString()).build();
		} catch (Exception e) {
			System.err.println("Error fetching data-");
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}
	
	@POST
	@Path("/CreateVirtualCard")
	public Response createVirtualCard(InputStream incomingData) {
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
			VirtualCardMaster virtualCardMaster = new VirtualCardMaster();
			virtualCardMaster.setAccountNo((String) json.get("accountNo"));
			virtualCardMaster.setCreatedTime(new Timestamp(System
					.currentTimeMillis()));
			virtualCardMaster.setIsActive('Y');
			virtualCardMaster.setCardNo(RandomOtp.generateCardNumber());
			//virtualCardMaster.set((String) json.get("customerId"));
			virtualCardMaster.setVirtualAddress((String) json
					.get("virtualAddress"));
		
			userTran.begin();
			em.persist(virtualCardMaster);
			VirtualCardPersonalDetails cardPersonalDetails = new VirtualCardPersonalDetails();
			cardPersonalDetails.setEmail((String) json.get("email"));
			//cardPersonalDetails.setGender((String) json.get("gender"));
			cardPersonalDetails.setMobileNo((String) json.get("mobileNo"));
			cardPersonalDetails.setName((String) json.get("name"));
			cardPersonalDetails.setStatus((String) json.get("status"));
			cardPersonalDetails.setCardId(virtualCardMaster.getID());
			//virtualCardMaster.setPersonalDetails(cardPersonalDetails);
			em.persist(cardPersonalDetails);
			userTran.commit();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("cardNo", virtualCardMaster.getCardNo());
			jsonObject.put("ID", virtualCardMaster.getID());
			jsonObject.put("accountNo", virtualCardMaster.getAccountNo());
			jsonObject.put("isActive", virtualCardMaster.getIsActive());
			jsonObject.put("virtualAddress", virtualCardMaster.getVirtualAddress());
			return Response.ok(jsonObject.toJSONString()).build();
		} catch (Exception e) {
			System.err.println("ERROR creating record-" + e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}
	
	@POST
	@Path("/AddRestriction")
	public Response addRestriction(InputStream incomingData) {

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
	@Path("/GetCardDetails")
	public Response getCardDetails(InputStream incomingData) {

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
	@Path("/GetTransactions")
	public Response getTransactions(InputStream incomingData) {

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
			System.err.println("ERROR creating record-"+e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}
	
	
	@POST
	@Path("/LimitMaster")
	public Response getLimits(InputStream incomingData) {

		EntityManager em = getEm();
		
		try {
			Query query =  em
					.createQuery("SELECT t FROM LimitMaster t", LimitMaster.class);
			List<LimitMaster> list =query.getResultList();
			JSONArray jsonArray = new JSONArray();
			if (list != null) {
				for (LimitMaster limitMaster : list) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", limitMaster.getId());
					jsonObject.put("limitType", limitMaster.getLimiType());
					jsonArray.put(jsonObject);
				}
			}

			return Response.ok(jsonArray.toString()).build();
		} catch (Exception e) {
			System.err.println("ERROR creating record-"+e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	
	@POST
	@Path("/SetLimits")
	public Response setLimits(InputStream incomingData) {
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
			VirtualCardRestrictions cardRestrictions = new VirtualCardRestrictions();
			cardRestrictions.setCardId(Integer.parseInt((String) json.get("cardId")));
			cardRestrictions.setParamKey((String) json.get("limitType"));
			cardRestrictions.setParamValue((String) json.get("value"));
		
			userTran.begin();
			em.persist(cardRestrictions);
			userTran.commit();
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("cardId", cardRestrictions.getCardId());
			jsonObject.put("ID", cardRestrictions.getId());
			jsonObject.put("limitType", cardRestrictions.getParamKey());
			jsonObject.put("value", cardRestrictions.getParamValue());
			return Response.ok(jsonObject.toJSONString()).build();
		} catch (Exception e) {
			System.err.println("ERROR creating record-" + e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}
}
