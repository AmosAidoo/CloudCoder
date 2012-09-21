// CloudCoder - a web-based pedagogical programming environment
// Copyright (C) 2011-2012, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011-2012, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.cloudcoder.repoapp.servlets;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cloudcoder.app.server.persist.BCrypt;
import org.cloudcoder.app.server.persist.Database;
import org.cloudcoder.app.shared.model.ConvertBytesToHex;
import org.cloudcoder.app.shared.model.ModelObjectField;
import org.cloudcoder.app.shared.model.ModelObjectUtil;
import org.cloudcoder.app.shared.model.OperationResult;
import org.cloudcoder.app.shared.model.SHA1;
import org.cloudcoder.app.shared.model.User;
import org.cloudcoder.app.shared.model.UserRegistrationRequest;
import org.cloudcoder.app.shared.model.UserRegistrationRequestStatus;
import org.cloudcoder.app.shared.model.json.JSONConversion;
import org.json.simple.JSONValue;

/**
 * Servlet allowing a new user to register.
 * 
 * @author David Hovemeyer
 */
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final SecureRandom random = new SecureRandom();
	
	private String smtpHost;
	private String smtpUsername;
	private String smtpPassword;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		smtpHost = config.getServletContext().getInitParameter("cloudcoder.repoapp.smtp.host");
		smtpUsername = config.getServletContext().getInitParameter("cloudcoder.repoapp.smtp.user");
		smtpPassword = config.getServletContext().getInitParameter("cloudcoder.repoapp.smtp.passwd");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getRequestDispatcher("_view/register.jsp").forward(req, resp);
	}
	
	private static final Set<ModelObjectField<? super UserRegistrationRequest, ?>> REQUIRED_ATTRIBUTES = new LinkedHashSet<ModelObjectField<? super UserRegistrationRequest, ?>>();
	static {
		REQUIRED_ATTRIBUTES.add(User.USERNAME);
		REQUIRED_ATTRIBUTES.add(User.FIRSTNAME);
		REQUIRED_ATTRIBUTES.add(User.LASTNAME);
		REQUIRED_ATTRIBUTES.add(User.EMAIL);
		REQUIRED_ATTRIBUTES.add(User.WEBSITE);
	}
	
	// POST is for handling AJAX requests
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		UserRegistrationRequest request = new UserRegistrationRequest();
		
		// Convert submitted form data into a User object
		for (ModelObjectField<? super UserRegistrationRequest, ?> field : User.SCHEMA.getFieldList()) {
			if (!REQUIRED_ATTRIBUTES.contains(field)) {
				continue;
			}
			
			String value = ServletUtil.getRequiredParam(req, "u_" + field.getName());
			if (value != null) {
				Object convertedValue = ModelObjectUtil.convertString(value, field.getType());
				field.setUntyped(request, convertedValue);
			}
		}
		
		// Get password. No need to check confirmation, since that's checked on
		// the client side.
		String password = ServletUtil.getRequiredParam(req, "u_password");
		request.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt(12)));
		
		// Generate a secret.
		SHA1 computeHash = new SHA1();
		computeHash.update(String.valueOf(random.nextLong()).getBytes("UTF-8"));
		for (ModelObjectField<? super UserRegistrationRequest, ?> field : REQUIRED_ATTRIBUTES) {
			computeHash.update(field.get(request).toString().getBytes("UTF-8"));
		}
		request.setSecret(new ConvertBytesToHex(computeHash.digest()).convert());
		
		// Status is PENDING.
		request.setStatus(UserRegistrationRequestStatus.PENDING);
		
		// Attempt to insert the request in the database
		OperationResult result = Database.getInstance().addUserRegistrationRequest(request);
		
		// If request was successfully added to database, then send an email
		if (result.isSuccess()) {
			// TODO: send email
			sendConfirmationEmail(request);
			result.setMessage("Please check your email to complete the registration.");
		}
		
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		JSONValue.writeJSONString(JSONConversion.convertOperationResultToJSON(result), resp.getWriter());
	}

	private void sendConfirmationEmail(UserRegistrationRequest request) {
		// TODO: send email
	}
}