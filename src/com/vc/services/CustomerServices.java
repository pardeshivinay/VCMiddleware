package com.vc.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
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
import com.vc.jpa.CustomerAccountMapping;
import com.vc.jpa.CustomerOTP;
import com.vc.util.HttpRequestHandler;
import com.vc.util.RandomOtp;

@Path("/CustomerServices")
public class CustomerServices {

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
	@Path("/AccountList")
	public Response getAccountList(InputStream incomingData) {

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
		try {
			Query query =  em
					.createQuery("SELECT t FROM CustomerAccountMapping t where t.customerId=:customerId", CustomerAccountMapping.class);
			query.setParameter("customerId", customerId);
			List<CustomerAccountMapping> list = query.getResultList();
			
			JSONArray jsonArray = new JSONArray();
			if (list != null) {
				for (CustomerAccountMapping customerAccMap : list) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("accountNo", customerAccMap.getAccountNo());
					jsonObject.put("customerId", customerAccMap.getCustomerId());
					jsonArray.put(jsonObject);
				}
			}

			return Response.ok(jsonArray.toString()).build();
		} catch (Exception e) {
			System.err.println("ERROR creating record-" + e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@POST
	@Path("/ValidateCustomer")
	public Response validateCustomer(InputStream incomingData) {

		init();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				incomingData));
		StringBuilder tabBankBuilder = new StringBuilder();
		EntityManager em = getEm();
		String userId = "";
		String pin = "";
		String line = null;
		try {
			while ((line = in.readLine()) != null) {
				tabBankBuilder.append(line);
			}
			System.out.println("tabBankBuilder="+ tabBankBuilder.toString());

			JSONParser jsonParser = new JSONParser();
			JSONObject json = (JSONObject) jsonParser.parse(tabBankBuilder
					.toString());
			userId = (String) json.get("userId");
			pin = (String) json.get("pin");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (userId != null && pin != null && !"".equals(userId)
				&& !"".equals(pin)) {
			try {
				Query query = em
						.createQuery(
								"SELECT t FROM Customer t where t.userId=:userId and t.pin=:pin",
								Customer.class);
				query.setParameter("userId", userId);
				query.setParameter("pin", pin);
				List<Customer> custList = query.getResultList();
				JSONArray jsonArray = new JSONArray();
				if (custList != null && !custList.isEmpty()) {
					Customer customer = custList.get(0);
					return Response.ok("{\"Result\":\"Login Successful\",\"resultCode\":\"01\",\"customerId\":\""+customer.getCustomerId()+"\"}").build();
				} else {
					return Response.ok("{\"Result\":\"Invalid login details\",\"resultCode\":\"00\"}").build();
				}
			} catch (Exception e) {
				System.err.println("ERROR creating record-" + e);
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			return Response.ok("{\"Result\":\"User Name/PIN cannot be empty\",\"resultCode\":\"00\"}").build();

		}
	}
	
	
	@POST
	@Path("/ValidateOTP")
	public Response validateOTP(InputStream incomingData) {

		init();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				incomingData));
		StringBuilder tabBankBuilder = new StringBuilder();
		EntityManager em = getEm();
		String customerId = "";
		String otp = "";
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
			otp = (String) json.get("otp");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (customerId != null && otp != null && !"".equals(customerId)
				&& !"".equals(otp)) {
			try {
				Query query = em
						.createQuery(
								"SELECT t FROM CustomerOTP t where t.customerId=:customerId and t.otp=:otp and isValid='Y'"
								+ " and TIMESTAMP(t.generated_time) > TIMESTAMP(:curr_time-30 MINUTE)",
								CustomerOTP.class);
				query.setParameter("customerId", customerId);
				query.setParameter("otp", otp);
				query.setParameter("curr_time", new Timestamp(System.currentTimeMillis()));
				List<CustomerOTP> otpList = query.getResultList();
				
				query = em.createQuery("SELECT t FROM CustomerOTP t where t.customerId=:customerId ");
				query.setParameter("customerId", customerId);
				List<CustomerOTP> otpListToUpdate = query.getResultList();
				for (CustomerOTP customerOTP : otpListToUpdate) {
					customerOTP.setIsValid('N');
					userTran.begin();
					em.persist(customerOTP);
					userTran.commit();
				}
				if (otpList != null && !otpList.isEmpty()) {
					return Response.ok("OTP validated successfully").build();
				} else {
					return Response.ok("Invalid OTP").build();
				}
			} catch (Exception e) {
				System.err.println("ERROR creating record-" + e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			return Response.ok("User Name/PIN cannot be empty").build();

		}
	}

	@POST
	@Path("/GenerateOTP")
	public Response generateOTP(InputStream incomingData) {
		 String message = System.getenv("OTP_MESSAGE");
		//String message = "Dear Customer Your OTP for QRYS User Activation is #OTP. - QRYS APP";
		 System.out.println("************ OTP Message ********* "+message);
		init();

		EntityManager em = getEm();
		String customerId = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				incomingData));
		StringBuilder tabBankBuilder = new StringBuilder();
		String line = null;
		RandomOtp randomotp = new RandomOtp();
		String otp = String.valueOf(randomotp.generatePin());
		System.out.println("OTP Generated - "+otp);
		message = message.replace("#OTP", otp);
		message = message.replace(" ", "+");
		try {
			while ((line = in.readLine()) != null) {
				System.out.println("in=" + in + ",incomingData=" + incomingData
						+ ",in.readLine()=" + in.readLine());
				tabBankBuilder.append(line);
			}
			JSONParser jsonParser = new JSONParser();
			JSONObject json = (JSONObject) jsonParser.parse(tabBankBuilder
					.toString());
			customerId = (String)json.get("customerId");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		try {
			Query query = em
					.createQuery("SELECT t FROM Customer t where t.customerId=:customerId", Customer.class);
			query.setParameter("customerId", customerId);
			List<Customer> list = query.getResultList();
			JSONArray jsonArray = new JSONArray();
			if (list != null && !list.isEmpty()) {
				Customer customer = list.get(0);
				sendMessage(customer.getMobileNo(), message);
				CustomerOTP customerOTP = new CustomerOTP();
				customerOTP.setCustomerId(customerId);
				customerOTP.setIsValid('Y');
				customerOTP.setOtp(otp);
				customerOTP.setCreatedTime(new Timestamp(System.currentTimeMillis()));
				userTran.begin();
				em.persist(customerOTP);
				userTran.commit();
			}

			return Response.ok("{\"Result\":\"OTP sent\",\"resultCode\":\"00\",\"OTP\":\""+otp+"\"}").build();
		} catch (Exception e) {
			System.err.println("ERROR creating record-" + e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@POST
	@Path("/Create")
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
			Customer customer = new Customer();
			customer.setFirstName((String) json.get("firstName"));
			customer.setGender((String) json.get("gender"));
			customer.setLastName((String) json.get("lastName"));
			customer.setMobileNo((String) json.get("mobileNo"));
			customer.setPin((String) json.get("pin"));
			customer.setUserId((String) json.get("userId"));

			userTran.begin();
			em.persist(customer);
			userTran.commit();
			return Response.ok(customer.toString()).build();
		} catch (Exception e) {
			System.err.println("ERROR creating record-" + e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}
	
	@POST
	@Path("/AddAccount")
	public Response addAccount(InputStream incomingData) {
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
			CustomerAccountMapping customerAccountMapping = new CustomerAccountMapping();
			customerAccountMapping.setAccountNo((String) json.get("accountNo"));
			customerAccountMapping.setCustomerId((String) json.get("customerId"));

			userTran.begin();
			em.persist(customerAccountMapping);
			userTran.commit();
			return Response.ok("Accounnt added").build();
		} catch (Exception e) {
			System.err.println("ERROR creating record-" + e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}
	
	
	public String sendMessage(String mobileNumber, String message) {
		System.out.println("sendMessage = " + message +mobileNumber);
		String smsURL = System.getenv("SMS_URL");
		//String smsURL = "http://enterprise.smsgupshup.com/GatewayAPI/rest?method=SendMessage&send_to=91#MOBILENO&msg=#MESSAGE&msg_type=TEXT&userid=2000125342&auth_scheme=plain&password=qwerty&v=1.1&format=text&mask=QRYSAP";
		smsURL = smsURL.replace("#MESSAGE", message);
		smsURL = smsURL.replace("#MOBILENO", mobileNumber);
		try {
			String smsResponse = new HttpRequestHandler(smsURL).requestGET();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*GetMethod method = null;
		try {
			//String msgURL = "http://enterprise.smsgupshup.com/GatewayAPI/rest?method=SendMessage&send_to=91$MOBILENO&msg=$MESSAGE&msg_type=TEXT&userid=2000125342&auth_scheme=plain&password=qwerty&v=1.1&format=text&mask=QRYSAP";
			String msgURL = System.getenv("SMS_URL");
			msgURL = msgURL.replace("#MESSAGE", message);
			msgURL = msgURL.replace("#MOBILENO", mobileNumber);
			System.out.println("URL for SMS - "+msgURL);
			HttpClient client = new HttpClient();
			method = new GetMethod(msgURL);
			// Provide custom retry handler is necessary
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(3, false));
			// Execute the method.
			int statusCode = client.executeMethod(method);
			System.out.println("statusCode = " + statusCode);
			if (statusCode != HttpStatus.SC_OK) {
				System.out.println("Method failed: " + method.getStatusLine());
			}
			// Read the response body.
			byte[] responseBody = method.getResponseBody();
			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary
			// data
			System.out.println(new String(responseBody));
		} catch (Exception e) {
			System.err.println("ERROR sending sms-" + e);
		}finally {
			method.releaseConnection();
		}*/
		return "success";
	}
}
