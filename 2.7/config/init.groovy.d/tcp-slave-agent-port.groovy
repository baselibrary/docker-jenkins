import hudson.model.*;
import jenkins.model.*;


Thread.start {
      sleep 10000
      Jenkins.instance.setSlaveAgentPort(50000)
      println "--> setting agent port for jnlp... done"
}