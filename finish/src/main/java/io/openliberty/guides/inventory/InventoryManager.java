// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
// tag::manager[]
package io.openliberty.guides.inventory;

import java.net.URL;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import javax.ws.rs.ProcessingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;

import io.openliberty.guides.inventory.client.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import io.openliberty.guides.inventory.model.InventoryList;
import io.openliberty.guides.inventory.model.SystemData;

@ApplicationScoped
public class InventoryManager {

  private List<SystemData> systems = Collections.synchronizedList(new ArrayList<>());
  private final String DEFAULT_PORT = System.getProperty("default.http.port");

  @Inject
  @RestClient
  private SystemClient defaultRestClient;

  public Properties get(String hostname) {
    Properties properties = null;
    if (hostname.equals("localhost")) {
      properties = getPropertiesWithDefaultHostName();
    } else {
      properties = getPropertiesWithGivenHostName(hostname);
    }

    return properties;
  }

  public void add(String hostname, Properties systemProps) {
    Properties props = new Properties();
    props.setProperty("os.name", systemProps.getProperty("os.name"));
    props.setProperty("user.name", systemProps.getProperty("user.name"));

    SystemData host = new SystemData(hostname, props);
    if (!systems.contains(host))
      systems.add(host);
  }

  public InventoryList list() {
    return new InventoryList(systems);
  }

  private Properties getPropertiesWithDefaultHostName() {
    try {

      final MyClient myClient = RestClientBuilder.newBuilder()
              .baseUrl(new URL("https://api.iextrading.com/1.0"))
              .build(MyClient.class);
      System.err.println("calling API...");
      final List<ChartEntry> chart = myClient.getChart("AAPL", "20190205");
      System.err.println("size = " + chart.size());
      chart.forEach(e -> System.err.println(String.format("%s %s %s", e.getDate(), e.getMinute(), e.getClose())));

      return defaultRestClient.getProperties();
    } catch (UnknownUrlException e) {
      System.err.println("The given URL is unreachable.");
    } catch (ProcessingException ex) {
      handleProcessingException(ex);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // tag::builder[]
  private Properties getPropertiesWithGivenHostName(String hostname) {
    String customURLString = "http://" + hostname + ":" + DEFAULT_PORT + "/system";
    URL customURL = null;
    try {
      customURL = new URL(customURLString);
      SystemClient customRestClient = RestClientBuilder.newBuilder()
                                         .baseUrl(customURL)
                                         .register(UnknownUrlExceptionMapper.class)
                                         .build(SystemClient.class);
      return customRestClient.getProperties();
    } catch (ProcessingException ex) {
      handleProcessingException(ex);
    } catch (UnknownUrlException e) {
      System.err.println("The given URL is unreachable.");
    } catch (MalformedURLException e) {
      System.err.println("The given URL is not formatted correctly.");
    }
    return null;
  }
  // end::builder[]

  private void handleProcessingException(ProcessingException ex) {
    Throwable rootEx = ExceptionUtils.getRootCause(ex);
    if (rootEx != null && rootEx instanceof UnknownHostException) {
      System.err.println("The specified host is unknown.");
    } else {
      throw ex;
    }
  }

}
// end::manager[]
