package fi.koku.kks.ui.common;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import fi.koku.services.entity.kks.v1.KksService;
import fi.koku.services.entity.kks.v1.KksServicePortType;

/**
 * Helper class for creating WS endpoints.
 * 
 * @author Ixonos / tuomape
 */
public class KokuWSFactory {
  private String uid;
  private String pwd;
  private String endpointBaseUrl;

  private final URL KKS_WSDL_LOCATION = getClass().getClassLoader().getResource("/wsdl/kksService.wsdl");
  private static final String KKS_SERVICE_VERSION = "0.0.1-SNAPSHOT";

  private final URL CUSTOMER_WSDL_LOCATION = getClass().getClassLoader().getResource("/wsdl/kksService.wsdl");
  private static final String CUSTOMER_SERVICE_VERSION = "0.0.1-SNAPSHOT";

  public KokuWSFactory(String uid, String pwd, String endpointBaseUrl) {
    this.uid = uid;
    this.pwd = pwd;
    this.endpointBaseUrl = endpointBaseUrl;
  }

  public KksServicePortType getKksService() {
    KksService service = new KksService(KKS_WSDL_LOCATION, new QName("http://services.koku.fi/entity/kks/v1",
        "kksService"));
    KksServicePortType kksServicePort = service.getKksServiceSoap11Port();
    String epAddr = endpointBaseUrl + "/kks-service-" + KKS_SERVICE_VERSION + "/KksServiceEndpointBean";

    ((BindingProvider) kksServicePort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
    ((BindingProvider) kksServicePort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, uid);
    ((BindingProvider) kksServicePort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, pwd);
    return kksServicePort;
  }

}
