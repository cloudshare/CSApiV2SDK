package com.cloudshare.api;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DTOs {

    public static class JsonObject {

        public String toString() {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                return "";
            }
        }

        public String toPrettyString() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                return "";
            }
        }
    }

    public static class ApiResponse extends JsonObject {
        public Object data;
        public int remaining_api_calls;

        public String status_code;
        public String status_text;
        public Object status_additional_data;

        private ObjectMapper mMapper = new ObjectMapper();

        public <T> T getDataAs(TypeReference<T> typeReference)
                throws IOException {
            String data = mMapper.writeValueAsString(this.data);
            return mMapper.readValue(data, typeReference);
        }
    }

    public static class EnvsListElement extends JsonObject {
        public String envId;
        public String envToken;

        public String name;
        public String description;

        public int status_code;
        public String status_text;

        public String organization;
        public String owner;
        public String licenseValid;
        public boolean invitationAllowed;
        public String expirationTime;
        public String view_url;

        public String snapshot;
        public String blueprint;
        public String project;
        public String environmentPolicy;
    }

    public static class DetailedEnvsListElement extends EnvsListElement {
        public List<VmStatus> vms;
    }

    public static class EnvStatus extends DetailedEnvsListElement {
        public AvailableActions available_actions;
        public EnvResources resources;
    }

    public static class EnvsList extends JsonObject {
        public List<EnvsListElement> envsList;
    }

    public static class VmStatus extends JsonObject {
        public String vmId;
        public String vmToken;
        public String username;
        public String password;
        public String name;
        public String description;
        public String os;
        public String IP;
        public String FQDN;
        public int status_code;
        public String status_text;
        public int progress;
        public String webAccessUrl;
        public String url;
        public String image_url;
    }

    public static class AvailableActions extends JsonObject {
        public boolean add_vms;
        public boolean delete_vm;
        public boolean reboot_vm;
        public boolean revert_vm;
        public boolean resume_environment;
        public boolean revert_environment;
        public boolean take_snapshot;
    }

    public static class EnvResources extends JsonObject {
        public int cpu_in_use;
        public int cpu_qouta;
        public int disk_size_in_use_mb;
        public int disk_size_qouta_mb;
        public int total_memory_in_use_mb;
        public int total_memory_qouta_mb;
    }

    public static class TemplatesListElement extends JsonObject {
        public String id;
        public String name;
        public String description;
        public int num_cpus;
        public int disk_size_gb;
        public int memory_size_mb;
        public String image_url;
        public boolean is_singleton;
        public int os_type;
        public String os_type_string;
        public String tags;
        public List<Object> categories;
    }

    public static class TemplatesList extends JsonObject {
        public List<TemplatesListElement> templatesList;
    }

    public static class SnapshotStatus extends JsonObject {
        public String Author;
        public String Comment;
        public String CreationTime;
        public boolean IsDefault;
        public boolean IsLatest;
        public String Name;
        public String SnapshotId;
    }

    public static class BlueprintStatus extends JsonObject {
        public String Name;
        public List<SnapshotStatus> Snapshots;
    }

    public static class EnvPolicyListElement extends JsonObject {
        public List<BlueprintStatus> Blueprints;
        public String EnvironmentPolicyDuration;
        public String EnvironmentPolicyId;
        public List<String> Organizations;
        public String Project;
    }

    public static class CloudFoldersStatus extends JsonObject {
        public String host;
        public String password;
        public String quota_in_use_gb;
        public String total_quota_gb;
        public String uri;
        public String user;
        public String private_folder_name;
    }

    public static class ExtendedCloudFoldersStatus extends CloudFoldersStatus {
        public String linuxFolder;
        public String mounted_folder_token;
        public String windowsFolder;
    }

    public static class DetailedCloudFoldersStatus extends ExtendedCloudFoldersStatus {
        public Boolean isActionComplete;
    }
    
    public static class RegeneratePasswordResult extends JsonObject{
        public String new_password;
        public String new_ftp_uri;
    }

    public static class BlueprintInfo extends JsonObject {
        public String ApiId;
        public String Name;
    }

    public static class LoginElement extends JsonObject {
        public String login_url;
    }
    
    public static class WhoAmIResult extends JsonObject {
        public String first_name;
        public String last_name;
        public String email;
        public String company;
        public String phone;
        public String job_title;
    }
    
    public static class ExecutePathResult {
        public String executed_path;
    }
    
    public static class ExecutePathExtResult {
        public String executionId;
    }
    
    public static class PostponeInactivityActionResult extends JsonObject{
    	public boolean is_success;
        public String message;
    }
    
    public static class CheckExecutionStatusResult {
        
        public int error_code;
        public boolean success;
        public String standard_output;
        public String standard_error;
        public String executed_path;
    }

    public static class RemoteAccessFileResult {
        public String rdp;
        public String clearTextPassword;
    }
    
    public static class EditMachineHardwareResult {
    	public boolean conflictsFound;
    	public List<String> conflicts;
    }
    
    public static class SnapshotDetails {
        public String snapshotId;
        public String name;
        public String creationTime;
        public String author;
        public String comment;
        public boolean isDefault;
        public boolean isLatest;
        public List<MachineDetails> machineList;
        public String url;

        public static class MachineDetails
        {
            public String name;
            public String os;
            public String internalAdresses;
            public Long memory_mb;
            public Long diskSize_mb;
            public Integer cpu_count;
            public String description;
            public String user;
            public String password;
        }
    }
}
