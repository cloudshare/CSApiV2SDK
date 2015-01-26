package com.cloudshare.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudshare.api.CSAPILowLevel.ApiException;
import com.cloudshare.api.DTOs.ApiResponse;
import com.cloudshare.api.DTOs.BlueprintInfo;
import com.cloudshare.api.DTOs.CheckExecutionStatusResult;
import com.cloudshare.api.DTOs.CloudFoldersStatus;
import com.cloudshare.api.DTOs.DetailedCloudFoldersStatus;
import com.cloudshare.api.DTOs.DetailedEnvsListElement;
import com.cloudshare.api.DTOs.EnvPolicyListElement;
import com.cloudshare.api.DTOs.EnvStatus;
import com.cloudshare.api.DTOs.EnvsListElement;
import com.cloudshare.api.DTOs.ExecutePathExtResult;
import com.cloudshare.api.DTOs.ExecutePathResult;
import com.cloudshare.api.DTOs.ExtendedCloudFoldersStatus;
import com.cloudshare.api.DTOs.LoginElement;
import com.cloudshare.api.DTOs.PostponeInactivityActionResult;
import com.cloudshare.api.DTOs.RegeneratePasswordResult;
import com.cloudshare.api.DTOs.RemoteAccessFileResult;
import com.cloudshare.api.DTOs.EditMachineHardwareResult;
import com.cloudshare.api.DTOs.SnapshotDetails;
import com.cloudshare.api.DTOs.SnapshotStatus;
import com.cloudshare.api.DTOs.TemplatesList;
import com.cloudshare.api.DTOs.TemplatesListElement;
import com.cloudshare.api.DTOs.VmStatus;
import com.cloudshare.api.DTOs.WhoAmIResult;
import com.fasterxml.jackson.core.type.TypeReference;

public class CSAPIHighLevel {

    private CSAPILowLevel mApi;

    public CSAPIHighLevel(String apiId, String apiKey) {
        this(apiId, apiKey, null, null);
    }

    public CSAPIHighLevel(String apiKey, String apiId, String apiVersion) {
        this(apiId, apiKey, apiVersion, null);
    }

    public CSAPIHighLevel(String apiKey, String apiId, String apiVersion,
            String baseUrl) {
        mApi = new CSAPILowLevel(apiKey, apiId, apiVersion, baseUrl);
    }

    public String getEnvDetailsUrl(EnvsListElement env) throws IOException,
            ApiException {
        List<EnvsListElement> envList = listEnvironments();
        for (EnvsListElement e : envList) {
            if (e.envId == env.envId) {
                return e.view_url;
            }
        }
        return null;
    }

    public List<EnvStatus> getEnvironmentStatusList() throws IOException,
            ApiException {
        return getEnvironmentStatusList("");
    }

    public List<EnvStatus> getEnvironmentStatusList(String filterSpecificUser)
            throws IOException, ApiException {
        List<EnvsListElement> envList = listEnvironments();

        List<EnvStatus> envStatusList = new ArrayList<>();
        for (EnvsListElement e : envList) {
            if (filterSpecificUser.equals("")
                    || filterSpecificUser.equalsIgnoreCase(e.owner)) {
                envStatusList.add(getEnvironmentState(e));
            }
        }

        return envStatusList;
    }

    public EnvStatus getEnvironmentState(EnvsListElement env)
            throws IOException, ApiException {
        HashMap<String, String> envStateParams = new HashMap<String, String>();
        envStateParams.put("EnvId", env.envId);

        ApiResponse response = mApi.callCSAPI("env", "GetEnvironmentState",
                envStateParams);
        return response.getDataAs(new TypeReference<EnvStatus>() {
        });
    }

    public List<SnapshotStatus> getSnapshots(EnvsListElement env)
            throws IOException, ApiException {
        HashMap<String, String> envStateParams = new HashMap<>();
        envStateParams.put("EnvId", env.envId);

        ApiResponse response = mApi.callCSAPI("env", "GetSnapshots",
                envStateParams);
        return response.getDataAs(new TypeReference<List<SnapshotStatus>>() {
        });
    }

    public List<EnvsListElement> listEnvironments() throws IOException,
            ApiException {
        ApiResponse res = mApi.callCSAPI("env", "ListEnvironments");
        return res.getDataAs(new TypeReference<List<EnvsListElement>>() {
        });
    }

    public List<EnvStatus> listEnvironmentsWithState() throws IOException,
            ApiException {
        ApiResponse res = mApi.callCSAPI("env", "ListEnvironmentsWithState");
        return res.getDataAs(new TypeReference<List<EnvStatus>>() {
        });
    }

    public SnapshotDetails getSnapshotDetails(String snapshotId)
            throws IOException, ApiException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("snapshotId", snapshotId);

        ApiResponse res = mApi.callCSAPI("env", "GetSnapshotDetails", params);
        return res.getDataAs(new TypeReference<SnapshotDetails>() {
        });
    }

    // General environment actions

    public void resumeEnvironment(EnvsListElement env) throws IOException,
            ApiException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        mApi.callCSAPI("env", "ResumeEnvironment", params);
    }

    public void revertEnvironment(EnvsListElement env) throws IOException,
            ApiException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        mApi.callCSAPI("env", "RevertEnvironment", params);
    }

    public void deleteEnvironment(EnvsListElement env) throws IOException,
            ApiException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        mApi.callCSAPI("env", "DeleteEnvironment", params);
    }

    public void extendEnvironment(EnvsListElement env) throws IOException,
            ApiException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        mApi.callCSAPI("env", "ExtendEnvironment", params);
    }
    
    public PostponeInactivityActionResult postponeInactivityAction(EnvsListElement env) throws IOException, ApiException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("EnvId", env.envId);
		ApiResponse res = mApi.callCSAPI("env", "PostponeInactivityAction", params);
		return res.getDataAs(new TypeReference<PostponeInactivityActionResult>() {});
	}

    public void suspendEnvironment(EnvsListElement env) throws IOException,
            ApiException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        mApi.callCSAPI("env", "SuspendEnvironment", params);
    }

    public void revertEnvironmentToSnapshot(EnvsListElement env,
            SnapshotStatus snapshot) throws IOException, ApiException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        mApi.callCSAPI("env", "RevertEnvironmentToSnapshot", params);
    }

    // Create environment actions

    public List<TemplatesListElement> listTemplates() throws IOException,
            ApiException {
        ApiResponse res = mApi.callCSAPI("env", "ListTemplates");
        return res.getDataAs(new TypeReference<TemplatesList>() {
        }).templatesList;
    }

    public void addVmFromTemplate(EnvsListElement env,
            TemplatesListElement template, String vmName, String vmDescription)
            throws IOException, ApiException {
        internalAddVMFromTemplate(env.envId, template.id, vmName, vmDescription);
    }

    private void internalAddVMFromTemplate(String envId, String templateId,
            String vmName, String vmDescription) throws IOException,
            ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", envId);
        params.put("TemplateVmId", templateId);
        params.put("VmName", vmName);
        params.put("VmDescription", vmDescription);

        mApi.callCSAPI("env", "AddVmFromTemplate", params);
    }

    public List<EnvPolicyListElement> createEntAppEnvOptions()
            throws IOException, ApiException {
        return createEntAppEnvOptions("", "", "");
    }

    public List<EnvPolicyListElement> createEntAppEnvOptions(
            String projectFilter) throws IOException, ApiException {
        return createEntAppEnvOptions(projectFilter, "", "");
    }

    public List<EnvPolicyListElement> createEntAppEnvOptions(
            String projectFilter, String blueprintFilter) throws IOException,
            ApiException {
        return createEntAppEnvOptions(projectFilter, blueprintFilter, "");
    }

    public List<EnvPolicyListElement> createEntAppEnvOptions(
            String projectFilter, String blueprintFilter,
            String environmentPolicyDurationFilter) throws IOException,
            ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ProjectFilter", projectFilter);
        params.put("BlueprintFilter", blueprintFilter);
        params.put("EnvironmentPolicyDurationFilter",
                environmentPolicyDurationFilter);

        ApiResponse res = mApi.callCSAPI("env", "CreateEntAppEnvOptions",
                params);
        return res.getDataAs(new TypeReference<List<EnvPolicyListElement>>() {
        });
    }

    public void createEntAppEnv(EnvPolicyListElement environmentPolicy,
            SnapshotStatus snapshot) throws IOException, ApiException {
        createEntAppEnv(environmentPolicy, snapshot, null, "", "", "");
    }

    public void createEntAppEnv(EnvPolicyListElement environmentPolicy,
            SnapshotStatus snapshot, String environmentNewName)
            throws IOException, ApiException {
        createEntAppEnv(environmentPolicy, snapshot, environmentNewName, "",
                "", "");
    }

    public void createEntAppEnv(EnvPolicyListElement environmentPolicy,
            SnapshotStatus snapshot, String environmentNewName,
            String projectFilter) throws IOException, ApiException {
        createEntAppEnv(environmentPolicy, snapshot, environmentNewName,
                projectFilter, "", "");
    }

    public void createEntAppEnv(EnvPolicyListElement environmentPolicy,
            SnapshotStatus snapshot, String environmentNewName,
            String projectFilter, String blueprintFilter) throws IOException,
            ApiException {
        createEntAppEnv(environmentPolicy, snapshot, environmentNewName,
                projectFilter, blueprintFilter, "");
    }

    public void createEntAppEnv(EnvPolicyListElement environmentPolicy,
            SnapshotStatus snapshot, String environmentNewName,
            String projectFilter, String blueprintFilter,
            String environmentPolicyDurationFilter) throws IOException,
            ApiException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvironmentPolicyId", environmentPolicy.EnvironmentPolicyId);
        params.put("SnapshotId", snapshot.SnapshotId);
        params.put("ProjectFilter", projectFilter);
        params.put("BlueprintFilter", blueprintFilter);
        params.put("EnvironmentPolicyDurationFilter",
                environmentPolicyDurationFilter);
        params.put("EnvironmentNewName", environmentNewName);

        mApi.callCSAPI("env", "CreateEntAppEnv", params);
    }

    public void createEmptyEntAppEnv(String envName, String projectName)
            throws IOException, ApiException {
        createEmptyEntAppEnv(envName, projectName, "none");
    }

    public void createEmptyEntAppEnv(String envName, String projectName,
            String description) throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvName", envName);
        params.put("ProjectName", projectName);

        mApi.callCSAPI("env", "CreateEmptyEntAppEnv", params);
    }

    // Snapshots

    public List<BlueprintInfo> getBlueprintsForPublish(EnvsListElement env)
            throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);

        ApiResponse res = mApi.callCSAPI("env", "GetBlueprintsForPublish",
                params);
        return res.getDataAs(new TypeReference<List<BlueprintInfo>>() {
        });
    }

    public void markSnapshotDefault(EnvsListElement env, SnapshotStatus snapshot)
            throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        params.put("SnapshotId", snapshot.SnapshotId);

        mApi.callCSAPI("env", "MarkSnapshotDefault", params);
    }

    public void entAppTakeSnapshot(EnvsListElement env, String snapshotName)
            throws IOException, ApiException {
        entAppTakeSnapshot(env, snapshotName, "", true);
    }

    public void entAppTakeSnapshot(EnvsListElement env, String snapshotName,
            String description) throws IOException, ApiException {
        entAppTakeSnapshot(env, snapshotName, description, true);
    }

    public void entAppTakeSnapshot(EnvsListElement env, String snapshotName,
            String description, boolean setAsDefault) throws IOException,
            ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        params.put("SnapshotName", snapshotName);
        params.put("Description", description);
        params.put("SetAsDefault", setAsDefault ? "true" : "false");

        mApi.callCSAPI("env", "EntAppTakeSnapshot", params);
    }

    public void entAppTakeSnapshotToNewBlueprint(EnvsListElement env,
            String snapshotName, String newBlueprintName) throws IOException,
            ApiException {
        entAppTakeSnapshotToNewBlueprint(env, snapshotName, newBlueprintName,
                "");
    }

    public void entAppTakeSnapshotToNewBlueprint(EnvsListElement env,
            String snapshotName, String newBlueprintName, String description/*
                                                                             * =
                                                                             * ""
                                                                             */)
            throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        params.put("SnapshotName", snapshotName);

        mApi.callCSAPI("env", "EntAppTakeSnapshotToNewBlueprint", params);
    }

    public void entAppTakeSnapshotToExistingBlueprint(EnvsListElement env,
            String snapshotName, BlueprintInfo otherBlueprint)
            throws IOException, ApiException {
        entAppTakeSnapshotToExistingBlueprint(env, snapshotName,
                otherBlueprint, "", true);
    }

    public void entAppTakeSnapshotToExistingBlueprint(EnvsListElement env,
            String snapshotName, BlueprintInfo otherBlueprint,
            String description/* = "" */) throws IOException, ApiException {
        entAppTakeSnapshotToExistingBlueprint(env, snapshotName,
                otherBlueprint, description, true);
    }

    public void entAppTakeSnapshotToExistingBlueprint(EnvsListElement env,
            String snapshotName, BlueprintInfo otherBlueprint,
            String description/* = "" */, boolean setAsDefault/* = true */)
            throws IOException, ApiException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        params.put("SnapshotName", snapshotName);
        params.put("OtherBlueprintId", otherBlueprint.ApiId);
        params.put("Description", description);
        params.put("SetAsDefault", setAsDefault ? "true" : "false");

        mApi.callCSAPI("env", "EntAppTakeSnapshotToExistingBlueprint", params);
    }

    // VM actions

    public void deleteVm(EnvsListElement env, VmStatus ms) throws IOException,
            ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        params.put("VmId", ms.vmId);

        mApi.callCSAPI("env", "DeleteVm", params);
    }

    public void revertVm(EnvsListElement env, VmStatus ms) throws IOException,
            ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        params.put("VmId", ms.vmId);

        mApi.callCSAPI("env", "RevertVm", params);
    }

    public void rebootVm(EnvsListElement env, VmStatus ms) throws IOException,
            ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        params.put("VmId", ms.vmId);

        mApi.callCSAPI("env", "RebootVm", params);
    }
    
    public boolean EditMachineHardware(EnvsListElement env, VmStatus vm, int numCpus, 
    		int memorySizeMBs, int diskSizeGBs) throws IOException, ApiException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("EnvId", env.envId);
        params.put("VmId", vm.vmId);
		if (numCpus>0)
			params.put("NumCpus", Integer.toString(numCpus));
		if (memorySizeMBs>0)
			params.put("MemorySizeMBs", Integer.toString(memorySizeMBs));
		if (diskSizeGBs>0)
			params.put("DiskSizeGBs", Integer.toString(diskSizeGBs));
		ApiResponse res = mApi.callCSAPI("env", "EditMachineHardware", params);
		return !res.getDataAs(new TypeReference<EditMachineHardwareResult>(){}).conflictsFound;
    }

    public RemoteAccessFileResult getRemoteAccessFile(EnvsListElement env,
            VmStatus ms, int desktopWidth, int desktopHeight, Boolean isConsole)
            throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        params.put("VmId", ms.vmId);
        params.put("desktopWidth", String.valueOf(desktopWidth));
        params.put("desktopHeight", String.valueOf(desktopHeight));

        if (isConsole != null) {
            params.put("isConsole", isConsole ? "true" : "false");
        }

        ApiResponse res = mApi.callCSAPI("env", "GetRemoteAccessFile", params);
        return res.getDataAs(new TypeReference<RemoteAccessFileResult>() {
        });
    }

    public ExecutePathResult executePath(EnvsListElement env, VmStatus ms,
            String path) throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        params.put("VmId", ms.vmId);
        params.put("path", path);

        ApiResponse res = mApi.callCSAPI("env", "ExecutePath", params);
        return res.getDataAs(new TypeReference<ExecutePathResult>() {
        });
    }
    
    public ExecutePathExtResult executePathExt(EnvsListElement env, VmStatus ms,
            String path) throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        params.put("VmId", ms.vmId);
        params.put("path", path);

        ApiResponse res = mApi.callCSAPI("env", "ExecutePathExt", params);
        return res.getDataAs(new TypeReference<ExecutePathExtResult>() {
        });
    }
    
    public CheckExecutionStatusResult checkExecutionStatus(EnvsListElement env, VmStatus ms,
            String executionId) throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);
        params.put("VmId", ms.vmId);
        params.put("ExecutionId", executionId);

        ApiResponse res = mApi.callCSAPI("env", "CheckExecutionStatus", params);
        return res.getDataAs(new TypeReference<CheckExecutionStatusResult>() {
        });
    }

    public DetailedCloudFoldersStatus mountAndFetchInfoExt(EnvsListElement env)
            throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);

        ApiResponse res = mApi.callCSAPI("env", "MountAndFetchInfoExt", params);
        return res.getDataAs(new TypeReference<DetailedCloudFoldersStatus>() {
        });
    }

    // CloudFolders

    public CloudFoldersStatus getCloudFoldersInfo() throws IOException,
            ApiException {
        ApiResponse res = mApi.callCSAPI("env", "GetCloudFoldersInfo");
        return res.getDataAs(new TypeReference<CloudFoldersStatus>() {
        });
    }
    
    public RegeneratePasswordResult regenerateCloudfoldersPassword() throws IOException, ApiException {
    	
		ApiResponse res = mApi.callCSAPI("env", "RegenerateCloudfoldersPassword");
		return res.getDataAs(new TypeReference<RegeneratePasswordResult>() {});
	}

    public void mount(EnvsListElement env) throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);

        mApi.callCSAPI("env", "Mount", params);
    }

    public void unmount(EnvsListElement env) throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);

        mApi.callCSAPI("env", "Unmount", params);
    }

    public ExtendedCloudFoldersStatus mountAndFetchInfo(EnvsListElement env)
            throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("EnvId", env.envId);

        ApiResponse res = mApi.callCSAPI("env", "MountAndFetchInfo", params);
        return res.getDataAs(new TypeReference<ExtendedCloudFoldersStatus>() {
        });
    }

    // Login

    public String getLoginUrl(String url) throws IOException, ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Url", url);

        ApiResponse res = mApi.callCSAPI("env", "GetLoginUrl", params);
        return res.getDataAs(new TypeReference<LoginElement>() {
        }).login_url;
    }
    
    public WhoAmIResult whoAmI(String userId) throws IOException, ApiException {
    	
    	Map<String, String> params = new HashMap<String, String>();
        params.put("UserId", userId);
        
        ApiResponse res = mApi.callCSAPI("env", "WhoAmI", params);
        return res.getDataAs(new TypeReference<WhoAmIResult>() {});
    }

    // Admin

    public List<String> listAllowedCommands() throws IOException, ApiException {
        ApiResponse res = mApi.callCSAPI("Admin", "ListAllowedCommands");
        return res.getDataAs(new TypeReference<List<String>>() {
        });
    }
}
