package org.ebayopensource.regression.internal.workflow

import java.io.File

import org.ebayopensource.regression.internal.datastore.BaseDataStore
import org.ebayopensource.regression.internal.http.{BaseHttpClient, HTTPResponse}
import org.ebayopensource.regression.internal.reader.{RequestEntry, YAMLTestStrategyReader}
import org.ebayopensource.regression.testrun.TestRunException
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.{Failure, Success, Try}
/**
  * Created by asfernando on 4/18/17.
  */
class RecordWorkflow(dataStore: BaseDataStore, httpClient: BaseHttpClient) {

  val logger = LoggerFactory.getLogger(classOf[RecordWorkflow])

  def recordState(testIdentifier: String, strategyFile: File): Try[String] = Try {
    YAMLTestStrategyReader.read(Source.fromFile(strategyFile).mkString.replace("\t","")) match {
      case Success(strategy) => {
        strategy.requests.foreach {
          request => {
            logger.info(s"Started request ${request.requestName}")
            request.continuation.isDefined match {
              case true => {
                WorkflowTools.performContinuations(testIdentifier, strategy, request, httpClient).flatMap {
                  httpResponses => recordResponse(testIdentifier, request, httpResponses, dataStore)
                } match {
                  case Success(message) => message
                  case Failure(exception) => throw exception
                }
              }
              case false => {
                WorkflowTools.performRequest(testIdentifier, strategy, request, httpClient).flatMap {
                  httpResponse => recordResponse(testIdentifier, request, Seq(httpResponse), dataStore)
                } match {
                  case Success(message) => message
                  case Failure(exception) => throw exception
                }
              }
            }
          }
        }
        s"${testIdentifier} was recorded successfully."
      }
      case Failure(t) => {
        throw new IllegalStateException(s"Failed to read test:= ${testIdentifier} from location ${strategyFile}. Reason is:= ${t}")
      }
    }
  }

  def recordResponse(testIdentifier: String, request: RequestEntry, httpResponses: Seq[HTTPResponse], dataStore: BaseDataStore): Try[String] = Try {
    (for {
      recordingEntries <- request.recorder.recordAndFilter(httpResponses)
      identifier <- dataStore.storeRequestRecording(testIdentifier, request.requestName, recordingEntries)
      awesome <- Try(s"Completed ${request.requestName} in test ${identifier} successfully")
    } yield (awesome)).recover {
      case e => throw new TestRunException(s"Could not record test: ${testIdentifier}. Failed in request: ${request.requestName}. Reason:= ${e}")
    } match {
      case Success(str) => str
      case Failure(t) => throw t
    }
  }
}
