package io.dscrapper;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.NotEmpty;

public class DscrapperConfiguration extends Configuration {
  @NotEmpty
  private String template;

  @NotEmpty
  private String defaultName = "Stranger";

  @JsonProperty
  public String getDefaultName() {
      return defaultName;
  }

  @JsonProperty
  public void setDefaultName(String defaultName) {
      this.defaultName = defaultName;
  }

  @JsonProperty
  public String getTemplate() {
      return template;
  }

  @JsonProperty
  public void setTemplate(String template) {
      this.template = template;
  }
}
