package com.vc.cbs.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import com.vc.jpa.Customer;
import com.vc.util.HttpRequestHandler;

@Path("/CBSServices")
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
	@Path("/GetCards")
	public Response getCards(InputStream incomingData) {

		BufferedReader in = new BufferedReader(new InputStreamReader(
				incomingData));
		StringBuilder tabBankBuilder = new StringBuilder();
		String line = null;
		String cardResponse ="";
		try {
			while ((line = in.readLine()) != null) {
				System.out.println("in=" + in + ",incomingData=" + incomingData
						+ ",in.readLine()=" + in.readLine());
				tabBankBuilder.append(line);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		EntityManager em = getEm();
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject json = (JSONObject) jsonParser.parse(tabBankBuilder
					.toString());
			System.out.println(json);
			String customerId = (String) json.get("customerId");
			init();
			Query query = em.createQuery(
					"SELECT t FROM Customer t where t.customerId=:customerId",
					Customer.class);
			query.setParameter("customerId", customerId);
			List<Customer> list = query.getResultList();
			JSONArray jsonArray = new JSONArray();
			// CBS URL
			// http://alphaiciapi2.mybluemix.net/rest/Card/getCardDetails/#customerId/#userId/#authCode
			if (list != null) {
				for (Customer customer : list) {
					String url = System.getenv("CARD_SERVICE_URL");
					String authurl = System.getenv("AUTH_URL");
					String authPasscode = System.getenv("AUTH_PASSCODE");
					String userId = System.getenv("CARD_USER_ID");
					String authUserId = System.getenv("AUTH_USER_ID");
					System.out.println("url="+url);
					System.out.println("authurl="+authurl);
					System.out.println("authPasscode="+authPasscode);
					System.out.println("userId="+userId);
					System.out.println("authUserId="+authUserId);
					authurl=authurl.replace("#passcode", authPasscode);
					authurl=authurl.replace("#userId", authUserId);
					System.out.println("authurl="+authurl);
					String tokenJSON = new HttpRequestHandler(authurl).requestGET();
					System.out.println("tokenJSON="+tokenJSON);
					// [{"token":"5a3901cfce84"}]
					JSONArray tokenArray = new JSONArray(tokenJSON);
					String token = (String) ((org.json.JSONObject) tokenArray.get(0))
							.get("token");
					System.out.println("token="+token);
					//String authCode = authurl.replace("#authCode", token);
					url=url.replace("#customerId", customer.getUserId());
					url=url.replace("#userId", userId);
					url=url.replace("#authCode", token);
					JSONObject jsonObject = new JSONObject();
					System.out.println("url="+url);
					cardResponse = new HttpRequestHandler(url).requestGET();
					System.out.println("cardResponse="+cardResponse);
				}
			}

			return Response.ok(cardResponse).build();
		} catch (Exception e) {
			System.err.println("ERROR creating record-" + e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	public static void main(String[] args) throws Exception {
		/*String token = "[{\"token\":\"5a3901cfce84\"}]";
		JSONArray tokenArray = new JSONArray(token);
		System.out.println(((JSONObject) tokenArray.get(0)).get("token"));*/
		
		String cardResponse = "{\"errorCode\":\"200\",\"errorDescripttion\":\"success\",\"cardDetails\":[{\"cardType\":\"Ruby Credit Card\",\"cardStatus\":\"Active\",\"current_balance\":96000.0,\"date_of_enrolemnt\":\"10/00/2014\",\"month_delinquency\":\"100321001110\",\"custId\":\"88881471\",\"expiry_date\":\"17/07/2025\",\"avail_lmt\":\"5000\"}]}";
	}

}
