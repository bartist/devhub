package nl.tudelft.ewi.devhub.server.backend;

import java.util.Iterator;

import lombok.extern.slf4j.Slf4j;

import org.apache.directory.api.ldap.codec.decorators.SearchResultEntryDecorator;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.message.BindRequest;
import org.apache.directory.api.ldap.model.message.BindRequestImpl;
import org.apache.directory.api.ldap.model.message.BindResponse;
import org.apache.directory.api.ldap.model.message.LdapResult;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.ResultCodeEnum;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Singleton;

@Slf4j
@Singleton
public class LdapBackend {
	
	public boolean authenticate(String netId, String password) {
		log.debug("Authenticating: {}", netId);
		
		try (LdapConnection conn = new LdapNetworkConnection("ldaps.tudelft.nl", 636, true)) {
			Preconditions.checkArgument(!Strings.isNullOrEmpty(password));
			
			BindRequest request = new BindRequestImpl();
			request.setSimple(true);
			request.setName(netId + "@tudelft.net");
			request.setCredentials(password);
			
			BindResponse response = conn.bind(request);
			LdapResult result = response.getLdapResult();
			ResultCodeEnum resultCode = result.getResultCode();
			
			conn.unBind();
			
			return resultCode == ResultCodeEnum.SUCCESS;
		}
		catch (Throwable e) {
			log.debug(e.getMessage(), e);
			return false;
		}
	}
	
	public void populateUserTable(String netId, String password) {
		log.debug("Populating user table using LDAP account: {}", netId);
		
		try (LdapConnection conn = new LdapNetworkConnection("ldaps.tudelft.nl", 636, true)) {
			BindRequest request = new BindRequestImpl();
			request.setSimple(true);
			request.setName(netId + "@tudelft.net");
			request.setCredentials(password);
			
			BindResponse response = conn.bind(request);
			LdapResult ldapResult = response.getLdapResult();
			ResultCodeEnum resultCode = ldapResult.getResultCode();
			if (resultCode != ResultCodeEnum.SUCCESS) {
				throw new IllegalStateException("Not authenticated!");
			}

			SearchCursor cursor = search(netId, conn);
			Iterator<Response> iterator = cursor.iterator();
			while (iterator.hasNext()) {
				Response searchResponse = iterator.next();
				SearchResultEntryDecorator decorator = (SearchResultEntryDecorator) searchResponse;
				Entry entry = decorator.getEntry();
				String email = entry.get("mail").toString();
				String name = entry.get("displayName").toString();
			}

			conn.unBind();
		}
		catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	private SearchCursor search(String netId, LdapConnection conn) throws LdapInvalidDnException, LdapException {
		SearchRequest searchRequest = new SearchRequestImpl();
		searchRequest.setBase(new Dn("OU=MDS,DC=tudelft,DC=net"));
		searchRequest.setScope(SearchScope.SUBTREE);
		searchRequest.setFilter("(uid=" + netId + ")");
		SearchCursor cursor = conn.search(searchRequest);
		return cursor;
	}
	
}