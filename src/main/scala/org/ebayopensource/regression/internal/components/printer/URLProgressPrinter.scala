package org.ebayopensource.regression.internal.components.printer
import org.ebayopensource.regression.application.aio.graph.AIOGraphContinuationPrinter
import org.ebayopensource.regression.internal.http.HTTPResponse
import org.slf4j.LoggerFactory

/**
  * Created by asfernando on 6/8/17.
  */
class URLProgressPrinter extends ProgressPrinter {

  val logger = LoggerFactory.getLogger(classOf[AIOGraphContinuationPrinter])

  override def printProgress(response: HTTPResponse): Unit = {
    logger.info(s"Calling URL ${response.request.url.toString}")
  }
}
