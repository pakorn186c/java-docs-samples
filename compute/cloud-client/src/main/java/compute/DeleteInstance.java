/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package compute;

// [START compute_instances_delete]

import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.Operation.Status;
import com.google.cloud.compute.v1.ZoneOperationsClient;
import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class DeleteInstance {

  public static void main(String[] args) throws IOException, InterruptedException {
    // TODO(developer): Replace these variables before running the sample.
    String project = "your-project-id";
    String zone = "zone-name";
    String instanceName = "instance-name";
    deleteInstance(project, zone, instanceName);
  }

  // Delete the instance specified by `instanceName`
  // if it's present in the given project and zone.
  public static void deleteInstance(String project, String zone, String instanceName)
      throws IOException, InterruptedException {
    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the `instancesClient.close()` method on the client to safely
    // clean up any remaining background resources.
    try (InstancesClient instancesClient = InstancesClient.create();
        ZoneOperationsClient zoneOperationsClient = ZoneOperationsClient.create()) {

      System.out.println(String.format("Deleting instance: %s ", instanceName));
      // Describe which instance is to be deleted.
      Operation response = instancesClient.delete(project, zone, instanceName);

      // Wait for the operation to complete.
      // Timeout is set at 3 minutes.
      LocalTime endTime = LocalTime.now().plusMinutes(3);
      while (response.getStatus() != Status.DONE
          && LocalTime.now().isBefore(endTime)) {
        response = zoneOperationsClient.get(project, zone, String.valueOf(response.getId()));
        TimeUnit.SECONDS.sleep(3);
      }

      if (response.hasError()) {
        System.out.println("Instance deletion failed ! ! " + response);
        return;
      }
      System.out.println("Operation Status: " + response.getStatus());
    }
  }
}
// [END compute_instances_delete]
