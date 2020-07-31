package com.example.st_25_sdk_sample;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import com.st.st25sdk.FastTransferModeInterface;
import com.st.st25sdk.Helper;
import com.st.st25sdk.MultiAreaInterface;
import com.st.st25sdk.NFCTag;
import com.st.st25sdk.RFReaderInterface;
import com.st.st25sdk.RegisterInterface;
import com.st.st25sdk.STException;
import com.st.st25sdk.STLog;
import com.st.st25sdk.STRegister;
import com.st.st25sdk.STException.STExceptionCode;
import com.st.st25sdk.STRegister.RegisterListener;
import com.st.st25sdk.STRegister.STRegisterField;
import com.st.st25sdk.TagHelper.ReadWriteProtection;
import com.st.st25sdk.command.FastTransferModeCommand;
import com.st.st25sdk.command.Iso15693CustomCommand;
import com.st.st25sdk.type5.STType5MultiAreaTag;
import com.st.st25sdk.type5.STType5PasswordInterface;
import com.st.st25sdk.type5.SysFileType5Extended;
import com.st.st25sdk.type5.st25dv.ST25DVDynRegisterEh;
import com.st.st25sdk.type5.st25dv.ST25DVDynRegisterEh.ST25DVEHControl;
import com.st.st25sdk.type5.st25dv.ST25DVDynRegisterGpo;
import com.st.st25sdk.type5.st25dv.ST25DVDynRegisterMb;
import com.st.st25sdk.type5.st25dv.ST25DVDynRegisterMb.ST25DVMBControl;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterEh;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterEndAi;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterGpo;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterITTime;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterLockCfg;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterMailboxWatchdog;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterMbMode;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterRfAiSS.ST25DVSecurityStatusPWDControl;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterRfMgt;
import com.st.st25sdk.type5.st25dv.ST25DVRegisterRfMgt.ST25DVRegisterRFMngtControl;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

public class ST25DVTag extends STType5MultiAreaTag implements STType5PasswordInterface, MultiAreaInterface, RegisterInterface, FastTransferModeInterface {
    public static final byte MAX_MEMORY_AREA_SUPPORTED = 4;
    public static final byte MAX_WRITE_MULTIPLE_BLOCKS = 4;
    public static final int MAILBOX_SIZE = 256;
    public static final int REGISTER_GPO_ADDRESS = 0;
    public static final int REGISTER_IT_TIME_ADDRESS = 1;
    public static final int REGISTER_EH_MODE_ADDRESS = 2;
    public static final int REGISTER_RF_MNGT_ADDRESS = 3;
    public static final int REGISTER_RFA1SS_ADDRESS = 4;
    public static final int REGISTER_ENDA1_ADDRESS = 5;
    public static final int REGISTER_RFA2SS_ADDRESS = 6;
    public static final int REGISTER_ENDA2_ADDRESS = 7;
    public static final int REGISTER_RFA3SS_ADDRESS = 8;
    public static final int REGISTER_ENDA3_ADDRESS = 9;
    public static final int REGISTER_RFA4SS_ADDRESS = 10;
    public static final int REGISTER_CCFILE_LOCK_ADDRESS = 12;
    public static final int REGISTER_MB_MODE_ADDRESS = 13;
    public static final int REGISTER_MB_WDG_ADDRESS = 14;
    public static final int REGISTER_LOCK_CFG_ADDRESS = 15;
    public static final int REGISTER_DYN_GPO_ADDRESS = 0;
    public static final int REGISTER_DYN_EH_CTRL_ADDRESS = 2;
    public static final int REGISTER_DYN_MB_CTRL_ADDRESS = 13;
    public static final int ST25DV_CONFIGURATION_PASSWORD_ID = 0;
    public static final int ST25DV_PASSWORD_1 = 1;
    public static final int ST25DV_PASSWORD_2 = 2;
    public static final int ST25DV_PASSWORD_3 = 3;
    private static final EnumMap<com.st.st25sdk.type5.st25dv.ST25DVTag.GpoCommand, Byte> mGpoCommandValue = new EnumMap(com.st.st25sdk.type5.st25dv.ST25DVTag.GpoCommand.class);
    private TreeMap<Integer, STRegister> mST25DVRegisterMap;
    private TreeMap<Integer, STRegister> mST25DVDynRegisterMap;
    protected ST25DVRegisterGpo mRegisterGpO;
    protected ST25DVRegisterITTime mRegisterItTime;
    protected ST25DVRegisterEh mRegisterEhMode;
    protected ST25DVRegisterRfMgt mRegisterRfMgt;
    protected ST25DVRegisterEndAi mRegisterEndArea1;
    protected ST25DVRegisterEndAi mRegisterEndArea2;
    protected ST25DVRegisterEndAi mRegisterEndArea3;
    protected ST25DVRegisterRfAiSS mRegisterRFA1SS;
    protected ST25DVRegisterRfAiSS mRegisterRFA2SS;
    protected ST25DVRegisterRfAiSS mRegisterRFA3SS;
    protected ST25DVRegisterRfAiSS mRegisterRFA4SS;
    protected ST25DVRegisterMbMode mRegisterMbMode;
    protected ST25DVRegisterMailboxWatchdog mRegisterMbWdg;
    protected ST25DVRegisterLockCfg mRegisterLockCfg;
    private ST25DVDynRegisterGpo mDynamicRegisterGpo;
    protected ST25DVDynRegisterEh mDynamicRegisterEhCtrl;
    private ST25DVDynRegisterMb mDynamicRegisterMbCtrl;
    protected FastTransferModeCommand ftmCmd;

    public ST25DVTag(RFReaderInterface readerInterface, byte[] uid) throws STException {
        this(readerInterface, uid, true);
    }

    public ST25DVTag(RFReaderInterface readerInterface, byte[] uid, boolean initDVFeatures) throws STException {
        super(readerInterface, uid);
        this.mName = "ST25DV";
        this.mTypeDescription = NFCTag.DYNAMIC_NFC_RFID_TAG;
        this.mType5Cmd.setIsWriteMultipleBlockSupported(true);
        this.mNdefCmd.setIsWriteMultipleBlockSupported(true);
        this.mSysFile = new SysFileType5Extended(this.mIso15693CustomCommand);
        this.mCache.add(this.mSysFile);
        if (initDVFeatures) {
            this.mST25DVRegisterMap = new TreeMap();
            this.initStaticRegisters(this.mIso15693CustomCommand);
            this.mST25DVDynRegisterMap = new TreeMap();
            this.initDynamicRegisters(this.mIso15693CustomCommand);
            this.ftmCmd = new FastTransferModeCommand(this.mIso15693CustomCommand, this.getUid(), 256);
            this.initAreaList();
        }

        int memSizeInBlocks = this.getNumberOfBlocks();
        this.setMaxReadMultipleBlocksReturned(memSizeInBlocks);
    }

    private void initStaticRegisters(Iso15693CustomCommand cmd) {
        this.mRegisterGpO = ST25DVRegisterGpo.newInstance(cmd);
        this.mRegisterItTime = ST25DVRegisterITTime.newInstance(cmd);
        this.mRegisterEhMode = ST25DVRegisterEh.newInstance(cmd);
        this.mRegisterRfMgt = ST25DVRegisterRfMgt.newInstance(cmd);
        this.mRegisterEndArea1 = ST25DVRegisterEndAi.newInstance(cmd, 1);
        this.mRegisterEndArea1.addRegisterListener(new RegisterListener() {
            public void registerChange() throws STException {
                com.example.st_25_sdk_sample.ST25DVTag.this.mRegisterEndArea1.invalidateCache();
                com.example.st_25_sdk_sample.ST25DVTag.this.initAreaList();
            }
        });
        this.mRegisterEndArea2 = ST25DVRegisterEndAi.newInstance(cmd, 2);
        this.mRegisterEndArea2.addRegisterListener(new RegisterListener() {
            public void registerChange() throws STException {
                com.example.st_25_sdk_sample.ST25DVTag.this.mRegisterEndArea2.invalidateCache();
                com.example.st_25_sdk_sample.ST25DVTag.this.initAreaList();
            }
        });
        this.mRegisterEndArea3 = ST25DVRegisterEndAi.newInstance(cmd, 3);
        this.mRegisterEndArea3.addRegisterListener(new RegisterListener() {
            public void registerChange() throws STException {
                com.example.st_25_sdk_sample.ST25DVTag.this.mRegisterEndArea3.invalidateCache();
                com.example.st_25_sdk_sample.ST25DVTag.this.initAreaList();
            }
        });
        this.mRegisterRFA1SS = ST25DVRegisterRfAiSS.newInstance(cmd, 1);
        this.mRegisterRFA2SS = ST25DVRegisterRfAiSS.newInstance(cmd, 2);
        this.mRegisterRFA3SS = ST25DVRegisterRfAiSS.newInstance(cmd, 3);
        this.mRegisterRFA4SS = ST25DVRegisterRfAiSS.newInstance(cmd, 4);
        this.mRegisterMbMode = ST25DVRegisterMbMode.newInstance(cmd);
        this.mRegisterMbWdg = ST25DVRegisterMailboxWatchdog.newInstance(cmd);
        this.mRegisterLockCfg = ST25DVRegisterLockCfg.newInstance(cmd);
        this.mST25DVRegisterMap.put(0, this.mRegisterGpO);
        this.mST25DVRegisterMap.put(1, this.mRegisterItTime);
        this.mST25DVRegisterMap.put(2, this.mRegisterEhMode);
        this.mST25DVRegisterMap.put(3, this.mRegisterRfMgt);
        this.mST25DVRegisterMap.put(4, this.mRegisterRFA1SS);
        this.mST25DVRegisterMap.put(5, this.mRegisterEndArea1);
        this.mST25DVRegisterMap.put(6, this.mRegisterRFA2SS);
        this.mST25DVRegisterMap.put(7, this.mRegisterEndArea2);
        this.mST25DVRegisterMap.put(8, this.mRegisterRFA3SS);
        this.mST25DVRegisterMap.put(9, this.mRegisterEndArea3);
        this.mST25DVRegisterMap.put(10, this.mRegisterRFA4SS);
        this.mST25DVRegisterMap.put(13, this.mRegisterMbMode);
        this.mST25DVRegisterMap.put(14, this.mRegisterMbWdg);
        this.mST25DVRegisterMap.put(15, this.mRegisterLockCfg);
        Iterator var2 = this.mST25DVRegisterMap.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<Integer, STRegister> entry = (Entry)var2.next();
            this.mCache.add(entry.getValue());
        }

    }

    private void initDynamicRegisters(Iso15693CustomCommand cmd) {
        this.mDynamicRegisterGpo = ST25DVDynRegisterGpo.newInstance(cmd);
        this.mDynamicRegisterEhCtrl = ST25DVDynRegisterEh.newInstance(cmd);
        this.mDynamicRegisterMbCtrl = ST25DVDynRegisterMb.newInstance(cmd);
        this.mST25DVDynRegisterMap.put(0, this.mDynamicRegisterGpo);
        this.mST25DVDynRegisterMap.put(2, this.mDynamicRegisterEhCtrl);
        this.mST25DVDynRegisterMap.put(13, this.mDynamicRegisterMbCtrl);
        Iterator var2 = this.mST25DVDynRegisterMap.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<Integer, STRegister> entry = (Entry)var2.next();
            this.mCache.add(entry.getValue());
        }

    }

    public boolean isVccOn() throws STException {
        String fieldName = ST25DVEHControl.VCC_ON.toString();
        STRegisterField fieldVccOn = this.mDynamicRegisterEhCtrl.getRegisterField(fieldName);
        return fieldVccOn.getValue() == 1;
    }

    public void refreshMailboxStatus() throws STException {
        this.refreshMailboxStatus(false);
    }

    public void refreshMailboxStatus(boolean useFastCommand) throws STException {
        this.mDynamicRegisterMbCtrl.invalidateCache();
        this.mDynamicRegisterMbCtrl.getRegisterValue(useFastCommand);
    }

    public void enableMailbox() throws STException {
        if (!this.mRegisterMbMode.isMBModeEnabled()) {
            this.mRegisterMbMode.setMBMode(true);
        }

        this.mDynamicRegisterMbCtrl.setMB(ST25DVMBControl.MB_EN, true);
    }

    public void disableMailbox() throws STException {
        this.invalidateCache();
        if (this.mRegisterMbMode.isMBModeEnabled()) {
            this.mDynamicRegisterMbCtrl.setMB(ST25DVMBControl.MB_EN, false);
        }

    }

    public void resetMailbox() throws STException {
        this.disableMailbox();
        this.enableMailbox();
    }

    public boolean isMailboxEnabled(boolean refresh) throws STException {
        return this.isMailboxEnabled(refresh, false);
    }

    public boolean hasHostPutMsg(boolean refresh) throws STException {
        return this.hasHostPutMsg(refresh, false);
    }

    public boolean hasRFPutMsg(boolean refresh) throws STException {
        return this.hasRFPutMsg(refresh, false);
    }

    public boolean hasHostMissMsg(boolean refresh) throws STException {
        return this.hasHostMissMsg(refresh, false);
    }

    public boolean hasRFMissMsg(boolean refresh) throws STException {
        return this.hasRFMissMsg(refresh, false);
    }

    public boolean isMailboxEnabled(boolean refresh, boolean useFastCommand) throws STException {
        return this.isMbCtrlEnable(ST25DVMBControl.MB_EN, refresh, useFastCommand);
    }

    public boolean hasHostPutMsg(boolean refresh, boolean useFastCommand) throws STException {
        return this.isMbCtrlEnable(ST25DVMBControl.HOST_PUT_MSG, refresh, useFastCommand);
    }

    public boolean hasRFPutMsg(boolean refresh, boolean useFastCommand) throws STException {
        return this.isMbCtrlEnable(ST25DVMBControl.RF_PUT_MSG, refresh, useFastCommand);
    }

    public boolean hasHostMissMsg(boolean refresh, boolean useFastCommand) throws STException {
        return this.isMbCtrlEnable(ST25DVMBControl.HOST_MISS_MSG, refresh, useFastCommand);
    }

    public boolean hasRFMissMsg(boolean refresh, boolean useFastCommand) throws STException {
        return this.isMbCtrlEnable(ST25DVMBControl.RF_MISS_MSG, refresh, useFastCommand);
    }

    private boolean isMbCtrlEnable(ST25DVMBControl mbCtrlField, boolean refresh, boolean useFastCommand) throws STException {
        if (refresh) {
            this.mDynamicRegisterMbCtrl.invalidateCache();
        }

        return this.mDynamicRegisterMbCtrl.isMBFieldEnabled(mbCtrlField, useFastCommand);
    }

    private boolean isPasswordNumberValid(int passwordNumber) {
        return passwordNumber >= 0 && passwordNumber <= 3;
    }

    public int getPasswordLength(int passwordNumber) throws STException {
        if (!this.isPasswordNumberValid(passwordNumber)) {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        } else {
            return 64;
        }
    }

    public void writePassword(int passwordNumber, byte[] newPassword) throws STException {
        if (!this.isPasswordNumberValid(passwordNumber)) {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        } else {
            this.mIso15693CustomCommand.writePwd((byte)passwordNumber, newPassword);
        }
    }

    public void writePassword(int passwordNumber, byte[] newPassword, byte flag) throws STException {
        if (!this.isPasswordNumberValid(passwordNumber)) {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        } else {
            this.mIso15693CustomCommand.writePwd((byte)passwordNumber, newPassword, flag, this.mUid);
        }
    }

    public void presentPassword(int passwordNumber, byte[] password) throws STException {
        if (!this.isPasswordNumberValid(passwordNumber)) {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        } else {
            this.mIso15693CustomCommand.presentPwd((byte)passwordNumber, password);
        }
    }

    public void presentPassword(int passwordNumber, byte[] password, byte flag) throws STException {
        if (!this.isPasswordNumberValid(passwordNumber)) {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        } else {
            this.mIso15693CustomCommand.presentPwd((byte)passwordNumber, password, flag, this.mUid);
        }
    }

    public int getConfigurationPasswordNumber() throws STException {
        return 0;
    }

    public byte writeMultipleBlock(int blockAddress, byte[] buffer) throws STException {
        if (buffer == null) {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        } else {
            return this.writeMultipleBlock(blockAddress, buffer.length / this.getBlockSizeInBytes() - 1, buffer);
        }
    }

    public byte writeMultipleBlock(int blockAddress, int numberOfBlocks, byte[] buffer) throws STException {
        return this.writeMultipleBlock(blockAddress, numberOfBlocks, buffer, this.mIso15693Cmd.getFlag());
    }

    public byte writeMultipleBlock(int blockAddress, int numberOfBlocks, byte[] buffer, byte flag) throws STException {
        if (blockAddress >= 0 && blockAddress <= 255) {
            if (numberOfBlocks >= 0 && numberOfBlocks < 4) {
                if (buffer != null && buffer.length != 0) {
                    if (buffer.length != (numberOfBlocks + 1) * this.getBlockSizeInBytes()) {
                        throw new STException(STExceptionCode.BAD_PARAMETER);
                    } else {
                        return this.mIso15693Cmd.writeMultipleBlock((byte)blockAddress, (byte)numberOfBlocks, buffer, flag, this.mUid);
                    }
                } else {
                    throw new STException(STExceptionCode.BAD_PARAMETER);
                }
            } else {
                throw new STException(STExceptionCode.BAD_PARAMETER);
            }
        } else {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        }
    }

    public byte extendedWriteMultipleBlock(int blockAddress, byte[] buffer) throws STException {
        if (buffer == null) {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        } else {
            return this.extendedWriteMultipleBlock(blockAddress, buffer.length / this.getBlockSizeInBytes() - 1, buffer);
        }
    }

    public byte extendedWriteMultipleBlock(int blockAddress, int numberOfBlocks, byte[] buffer) throws STException {
        return this.extendedWriteMultipleBlock(blockAddress, numberOfBlocks, buffer, this.mIso15693Cmd.getFlag());
    }

    public byte extendedWriteMultipleBlock(int blockAddress, int numberOfBlocks, byte[] buffer, byte flag) throws STException {
        if (blockAddress >= 0 && blockAddress <= 65535) {
            if (numberOfBlocks >= 0 && numberOfBlocks < 4) {
                if (buffer != null && buffer.length != 0) {
                    if (buffer.length != (numberOfBlocks + 1) * this.getBlockSizeInBytes()) {
                        throw new STException(STExceptionCode.BAD_PARAMETER);
                    } else {
                        return this.mIso15693Cmd.extendedWriteMultipleBlock(Helper.convertIntTo2BytesHexaFormat(blockAddress), Helper.convertIntTo2BytesHexaFormat(numberOfBlocks), buffer, flag, this.mUid);
                    }
                } else {
                    throw new STException(STExceptionCode.BAD_PARAMETER);
                }
            } else {
                throw new STException(STExceptionCode.BAD_PARAMETER);
            }
        } else {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        }
    }

    public byte[] extendedGetMultipleBlockSecurityStatus(int blockAddress, int numberOfBlocks) throws STException {
        return this.extendedGetMultipleBlockSecurityStatus(blockAddress, numberOfBlocks, this.mIso15693Cmd.getFlag());
    }

    public byte[] extendedGetMultipleBlockSecurityStatus(int blockAddress, int numberOfBlocks, byte flag) throws STException {
        if (blockAddress >= 0 && blockAddress <= 65535) {
            if (numberOfBlocks >= 0 && numberOfBlocks <= 65535) {
                return this.mIso15693Cmd.extendedGetMultipleBlockSecStatus(Helper.convertIntTo2BytesHexaFormat(blockAddress), Helper.convertIntTo2BytesHexaFormat(numberOfBlocks), flag, this.getUid());
            } else {
                throw new STException(STExceptionCode.BAD_PARAMETER);
            }
        } else {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        }
    }

    public byte[] fastExtendedReadSingleBlock(int blockAddress) throws STException {
        return this.fastExtendedReadSingleBlock(blockAddress, this.mIso15693CustomCommand.getFlag());
    }

    public byte[] fastExtendedReadSingleBlock(int blockAddress, byte flag) throws STException {
        return this.fastExtendedReadMultipleBlock(blockAddress, 0, flag);
    }

    public byte[] fastExtendedReadMultipleBlock(int blockAddress, int numberOfBlocks) throws STException {
        return this.fastExtendedReadMultipleBlock(blockAddress, numberOfBlocks, this.mIso15693CustomCommand.getFlag());
    }

    public byte[] fastExtendedReadMultipleBlock(int blockAddress, int numberOfBlocks, byte flag) throws STException {
        if (blockAddress >= 0 && blockAddress <= 65535) {
            if (numberOfBlocks >= 0 && numberOfBlocks <= 65535) {
                return this.mIso15693CustomCommand.fastExtendedReadMultipleBlock(Helper.convertIntTo2BytesHexaFormat(blockAddress), Helper.convertIntTo2BytesHexaFormat(numberOfBlocks), flag, this.getUid());
            } else {
                throw new STException(STExceptionCode.BAD_PARAMETER);
            }
        } else {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        }
    }

    public byte[] readConfig(int configId) throws STException {
        return this.readConfig(configId, this.mIso15693CustomCommand.getFlag());
    }

    public byte[] readConfig(int configId, byte flag) throws STException {
        return this.mIso15693CustomCommand.readConfig((byte)configId, flag, this.getUid());
    }

    public byte writeConfig(int configId, byte value) throws STException {
        return this.writeConfig(configId, value, this.mIso15693CustomCommand.getFlag());
    }

    public byte writeConfig(int configId, byte value, byte flag) throws STException {
        return this.mIso15693CustomCommand.writeConfig((byte)configId, value, flag, this.getUid());
    }

    public byte[] readDynConfig(int configId) throws STException {
        return this.readDynConfig(configId, this.mIso15693CustomCommand.getFlag());
    }

    public byte[] readDynConfig(int configId, byte flag) throws STException {
        return this.mIso15693CustomCommand.readDynConfig((byte)configId, flag, this.getUid());
    }

    public byte writeDynConfig(int configId, byte value) throws STException {
        return this.writeDynConfig(configId, value, this.mIso15693CustomCommand.getFlag());
    }

    public byte writeDynConfig(int configId, byte value, byte flag) throws STException {
        return this.mIso15693CustomCommand.writeDynConfig((byte)configId, value, flag, this.getUid());
    }

    public byte[] fastReadDynConfig(int configId) throws STException {
        return this.mIso15693CustomCommand.fastReadDynConfig((byte)configId);
    }

    public byte fastWriteDynConfig(int configId, byte value) throws STException {
        return this.mIso15693CustomCommand.fastWriteDynConfig((byte)configId, value);
    }

    public byte manageGpoCommand(com.st.st25sdk.type5.st25dv.ST25DVTag.GpoCommand cmd, byte flag) throws STException {
        return this.mIso15693CustomCommand.manageGpo((Byte)mGpoCommandValue.get(cmd), flag, this.mUid);
    }

    public byte writeMailboxMessage(byte[] buffer) throws STException {
        return this.ftmCmd.writeMailboxMessage(buffer);
    }

    public byte writeMailboxMessage(int size, byte[] buffer) throws STException {
        return this.ftmCmd.writeMailboxMessage(size, buffer);
    }

    public byte writeMailboxMessage(int size, byte[] buffer, byte flag) throws STException {
        return this.ftmCmd.writeMailboxMessage(size, buffer, flag);
    }

    public byte[] readMailboxMessage(int mbAddress, int size) throws STException {
        return this.ftmCmd.readMailboxMessage(mbAddress, size);
    }

    public byte[] readMailboxMessage(int mbAddress, int size, byte flag) throws STException {
        return this.ftmCmd.readMailboxMessage(mbAddress, size, flag);
    }

    public int readMailboxMessageLength() throws STException {
        return this.ftmCmd.readMailboxMessageLength();
    }

    public byte fastWriteMailboxMessage(byte[] buffer) throws STException {
        return this.ftmCmd.fastWriteMailboxMessage(buffer);
    }

    public byte fastWriteMailboxMessage(int size, byte[] buffer) throws STException {
        return this.ftmCmd.fastWriteMailboxMessage(size, buffer);
    }

    public byte fastWriteMailboxMessage(int size, byte[] buffer, byte flag) throws STException {
        return this.ftmCmd.fastWriteMailboxMessage(size, buffer, flag);
    }

    public byte[] fastReadMailboxMessage(int mbAddress, int size) throws STException {
        return this.ftmCmd.fastReadMailboxMessage(mbAddress, size);
    }

    public byte[] fastReadMailboxMessage(int mbAddress, int size, byte flag) throws STException {
        return this.ftmCmd.fastReadMailboxMessage(mbAddress, size, flag);
    }

    public int fastReadMailboxMessageLength() throws STException {
        return this.ftmCmd.fastReadMailboxMessageLength();
    }

    public int getMaxNumberOfAreas() {
        return 4;
    }

    public int getNumberOfAreas() throws STException {
        int numberOfAreas = 1;
        int maxEndOfAreaValue = Helper.convertByteToUnsignedInt(this.getMaxEndOfAreaValue());
        int endArea1 = Helper.convertByteToUnsignedInt(this.mRegisterEndArea1.getEndArea());
        int endArea2 = Helper.convertByteToUnsignedInt(this.mRegisterEndArea2.getEndArea());
        int endArea3 = Helper.convertByteToUnsignedInt(this.mRegisterEndArea3.getEndArea());
        if (endArea1 != maxEndOfAreaValue) {
            ++numberOfAreas;
        }

        if (endArea2 != maxEndOfAreaValue) {
            ++numberOfAreas;
        }

        if (endArea3 != maxEndOfAreaValue) {
            ++numberOfAreas;
        }

        return numberOfAreas;
    }

    public void setNumberOfAreas(int nbOfAreas) throws STException {
        throw new STException(STExceptionCode.NOT_SUPPORTED);
    }

    public int getAreaSizeInBytes(int area) throws STException {
        int areaSizeInBlocks = -1;
        switch(area) {
            case 1:
                areaSizeInBlocks = this.mRegisterEndArea1.getEndAreaInBlock() + 1;
                break;
            case 2:
                areaSizeInBlocks = this.mRegisterEndArea2.getEndAreaInBlock() - this.mRegisterEndArea1.getEndAreaInBlock();
                break;
            case 3:
                areaSizeInBlocks = this.mRegisterEndArea3.getEndAreaInBlock() - this.mRegisterEndArea2.getEndAreaInBlock();
                break;
            case 4:
                areaSizeInBlocks = this.getNumberOfBlocks() - 1 - this.mRegisterEndArea3.getEndAreaInBlock();
                break;
            default:
                throw new STException(STExceptionCode.BAD_PARAMETER);
        }

        return areaSizeInBlocks * this.getBlockSizeInBytes();
    }

    public int getAreaOffsetInBlocks(int area) throws STException {
        int areaOffsetInBlocks = -1;
        if (!this.isAreaNumberValid(area)) {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        } else {

            switch(area) {
                case 1:
                    areaOffsetInBlocks = 0;
                    break;
                case 2:
                    areaOffsetInBlocks = this.mRegisterEndArea1.getEndAreaInBlock() + 1;
                    break;
                case 3:
                    areaOffsetInBlocks = this.mRegisterEndArea2.getEndAreaInBlock() + 1;
                    break;
                case 4:
                    areaOffsetInBlocks = this.mRegisterEndArea3.getEndAreaInBlock() + 1;
                    break;
                default:
                    throw new STException(STExceptionCode.BAD_PARAMETER);
            }

            return areaOffsetInBlocks;
        }
    }

    public int getAreaOffsetInBytes(int area) throws STException {
        int areaOffsetInBytes = this.getAreaOffsetInBlocks(area) * this.getBlockSizeInBytes();
        return areaOffsetInBytes;
    }

    public int getAreaFromByteAddress(int address) throws STException {
        int ret = 1;
        int memSizeInBytes = this.getMemSizeInBytes();
        if (address >= 0 && address < memSizeInBytes) {
            int numberOfAreas = this.getNumberOfAreas();

            for(int areaID = 1; areaID <= numberOfAreas; ++areaID) {
                int startAddress = this.getAreaOffsetInBytes(areaID);
                int length = this.getAreaSizeInBytes(areaID);
                if (startAddress <= address && address < startAddress + length) {
                    return areaID;
                }
            }

            return ret;
        } else {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        }
    }

    public int getAreaPasswordLength(int area) throws STException {
        if (area >= 1 && area <= 4) {
            int passwordNumber = this.getPasswordNumber(area);
            if (passwordNumber == 0) {
                throw new STException(STExceptionCode.BAD_PARAMETER);
            } else {
                return this.getPasswordLength(passwordNumber);
            }
        } else {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        }
    }

    public int getPasswordNumber(int area) throws STException {
        ST25DVSecurityStatusPWDControl pwdControl;
        switch(area) {
            case 1:
                pwdControl = this.mRegisterRFA1SS.getSSPWDControl();
                break;
            case 2:
                pwdControl = this.mRegisterRFA2SS.getSSPWDControl();
                break;
            case 3:
                pwdControl = this.mRegisterRFA3SS.getSSPWDControl();
                break;
            case 4:
                pwdControl = this.mRegisterRFA4SS.getSSPWDControl();
                break;
            default:
                throw new STException(STExceptionCode.BAD_PARAMETER);
        }

        byte passwordNumber;
        switch(pwdControl) {
            case NO_PWD_SELECTED:
                passwordNumber = 0;
                break;
            case PROTECTED_BY_PWD1:
                passwordNumber = 1;
                break;
            case PROTECTED_BY_PWD2:
                passwordNumber = 2;
                break;
            case PROTECTED_BY_PWD3:
                passwordNumber = 3;
                break;
            default:
                throw new STException(STExceptionCode.BAD_PARAMETER);
        }

        return passwordNumber;
    }

    public void setPasswordNumber(int area, int passwordNumber) throws STException {
        if (!this.isPasswordNumberValid(passwordNumber)) {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        } else {
            ST25DVSecurityStatusPWDControl pwdControl;
            switch(passwordNumber) {
                case 0:
                    pwdControl = ST25DVSecurityStatusPWDControl.NO_PWD_SELECTED;
                    break;
                case 1:
                    pwdControl = ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD1;
                    break;
                case 2:
                    pwdControl = ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD2;
                    break;
                case 3:
                    pwdControl = ST25DVSecurityStatusPWDControl.PROTECTED_BY_PWD3;
                    break;
                default:
                    throw new STException(STExceptionCode.BAD_PARAMETER);
            }

            switch(area) {
                case 1:
                    this.mRegisterRFA1SS.setSSPWDControl(pwdControl);
                    break;
                case 2:
                    this.mRegisterRFA2SS.setSSPWDControl(pwdControl);
                    break;
                case 3:
                    this.mRegisterRFA3SS.setSSPWDControl(pwdControl);
                    break;
                case 4:
                    this.mRegisterRFA4SS.setSSPWDControl(pwdControl);
                    break;
                default:
                    throw new STException(STExceptionCode.BAD_PARAMETER);
            }

        }
    }

    public List<ReadWriteProtection> getPossibleReadWriteProtection(int area) throws STException {
        if (!this.isAreaNumberValid(area)) {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        } else {
            List<ReadWriteProtection> possibleReadWriteProtections = new ArrayList();
            if (area == 1) {
                possibleReadWriteProtections.add(ReadWriteProtection.READABLE_AND_WRITABLE);
                possibleReadWriteProtections.add(ReadWriteProtection.READABLE_AND_WRITE_PROTECTED_BY_PWD);
                possibleReadWriteProtections.add(ReadWriteProtection.READABLE_AND_WRITE_IMPOSSIBLE);
            } else {
                possibleReadWriteProtections.add(ReadWriteProtection.READABLE_AND_WRITABLE);
                possibleReadWriteProtections.add(ReadWriteProtection.READABLE_AND_WRITE_PROTECTED_BY_PWD);
                possibleReadWriteProtections.add(ReadWriteProtection.READ_AND_WRITE_PROTECTED_BY_PWD);
                possibleReadWriteProtections.add(ReadWriteProtection.READ_PROTECTED_BY_PWD_AND_WRITE_IMPOSSIBLE);
            }

            return possibleReadWriteProtections;
        }
    }

    public ReadWriteProtection getReadWriteProtection(int area) throws STException {
        ReadWriteProtection ss;
        switch(area) {
            case 1:
                ss = this.mRegisterRFA1SS.getSSRWProtection();
                break;
            case 2:
                ss = this.mRegisterRFA2SS.getSSRWProtection();
                break;
            case 3:
                ss = this.mRegisterRFA3SS.getSSRWProtection();
                break;
            case 4:
                ss = this.mRegisterRFA4SS.getSSRWProtection();
                break;
            default:
                throw new STException(STExceptionCode.BAD_PARAMETER);
        }

        return ss;
    }

    public void setReadWriteProtection(int area, ReadWriteProtection protection) throws STException {
        ST25DVRegisterRfAiSS reg;
        switch(area) {
            case 1:
                reg = this.mRegisterRFA1SS;
                break;
            case 2:
                reg = this.mRegisterRFA2SS;
                break;
            case 3:
                reg = this.mRegisterRFA3SS;
                break;
            case 4:
                reg = this.mRegisterRFA4SS;
                break;
            default:
                throw new STException(STExceptionCode.BAD_PARAMETER);
        }

        reg.setSSReadWriteProtection(protection);
    }

    public void setReadWriteProtection(int area, ReadWriteProtection protection, byte[] password) throws STException {
        if (area >= 1 && area <= 4) {
            this.presentPassword(0, password);
            this.setReadWriteProtection(area, protection);
        } else {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        }
    }

    public List<STRegister> getRegisterList() {
        return new ArrayList(this.mST25DVRegisterMap.values());
    }

    public List<STRegister> getDynamicRegisterList() {
        return new ArrayList(this.mST25DVDynRegisterMap.values());
    }

    public void refreshRegistersStatus() throws STException {
        Iterator var1 = this.mST25DVRegisterMap.entrySet().iterator();

        while(var1.hasNext()) {
            Entry<Integer, STRegister> entry = (Entry)var1.next();
            ((STRegister)entry.getValue()).invalidateCache();
            ((STRegister)entry.getValue()).getRegisterValue();
        }

    }

    public void refreshDynamicRegistersStatus() throws STException {
        Iterator var1 = this.mST25DVDynRegisterMap.entrySet().iterator();

        while(var1.hasNext()) {
            Entry<Integer, STRegister> entry = (Entry)var1.next();
            ((STRegister)entry.getValue()).invalidateCache();
            ((STRegister)entry.getValue()).getRegisterValue();
        }

    }

    public STRegister getRegister(int registerAddress) {
        return (STRegister)this.mST25DVRegisterMap.get(registerAddress);
    }

    public STRegister getDynamicRegister(int registerAddress) {
        return (STRegister)this.mST25DVDynRegisterMap.get(registerAddress);
    }

    public ST25DVRegisterEndAi getRegisterEndArea1() {
        return this.mRegisterEndArea1;
    }

    public ST25DVRegisterEndAi getRegisterEndArea2() {
        return this.mRegisterEndArea2;
    }

    public ST25DVRegisterEndAi getRegisterEndArea3() {
        return this.mRegisterEndArea3;
    }

    public ST25DVRegisterEndAi getRegisterEndArea(int area) throws STException {
        switch(area) {
            case 1:
                return this.mRegisterEndArea1;
            case 2:
                return this.mRegisterEndArea2;
            case 3:
                return this.mRegisterEndArea3;
            default:
                throw new STException(STExceptionCode.BAD_PARAMETER);
        }
    }

    public void setAreaEndValues(byte endOfArea1, byte endOfArea2, byte endOfArea3) throws STException {
        byte maxEndOfAreaValue = this.getMaxEndOfAreaValue();
        int endA1 = Helper.convertByteToUnsignedInt(endOfArea1);
        int endA2 = Helper.convertByteToUnsignedInt(endOfArea2);
        int endA3 = Helper.convertByteToUnsignedInt(endOfArea3);
        STLog.i("Current values:");
        STLog.i("endOfArea1 : " + String.format("%02x", this.getRegisterEndArea1().getRegisterValue()).toUpperCase());
        STLog.i("endOfArea2 : " + String.format("%02x", this.getRegisterEndArea2().getRegisterValue()).toUpperCase());
        STLog.i("endOfArea3 : " + String.format("%02x", this.getRegisterEndArea3().getRegisterValue()).toUpperCase());
        STLog.i("New values:");
        STLog.i("endOfArea1 : " + Helper.convertByteToHexString(endOfArea1));
        STLog.i("endOfArea2 : " + Helper.convertByteToHexString(endOfArea2));
        STLog.i("endOfArea3 : " + Helper.convertByteToHexString(endOfArea3));
        if (endA1 <= endA2 && endA2 <= endA3) {
            if (endOfArea1 == endOfArea2 && endOfArea1 != maxEndOfAreaValue) {
                throw new STException(STExceptionCode.BAD_PARAMETER);
            } else if (endOfArea2 == endOfArea3 && endOfArea2 != maxEndOfAreaValue) {
                throw new STException(STExceptionCode.BAD_PARAMETER);
            } else {
                if (this.getRegisterEndArea3().getRegisterValue() != Helper.convertByteToUnsignedInt(maxEndOfAreaValue)) {
                    this.getRegisterEndArea3().setRegisterValue(Helper.convertByteToUnsignedInt(maxEndOfAreaValue));
                }

                if (this.getRegisterEndArea2().getRegisterValue() != Helper.convertByteToUnsignedInt(maxEndOfAreaValue)) {
                    this.getRegisterEndArea2().setRegisterValue(Helper.convertByteToUnsignedInt(maxEndOfAreaValue));
                }

                this.getRegisterEndArea1().setRegisterValue(endA1);
                if (endA2 > endA1) {
                    this.getRegisterEndArea2().setRegisterValue(endA2);
                }

                if (endA3 > endA2) {
                    this.getRegisterEndArea3().setRegisterValue(endA3);
                }

            }
        } else {
            throw new STException(STExceptionCode.BAD_PARAMETER);
        }
    }

    public byte getMaxEndOfAreaValue() throws STException {
        int memSizeInBlocks = this.getMemSizeInBytes() / this.getBlockSizeInBytes();
        int addressOfLastBlock = memSizeInBlocks - 1;
        int maxEndOfAreaValue = (addressOfLastBlock - 7) / 8;
        return (byte)maxEndOfAreaValue;
    }

    public void lockConfiguration() throws STException {
        this.mRegisterLockCfg.setLockCfgMode(true);
    }

    public void killRf(com.st.st25sdk.type5.st25dv.ST25DVTag.RfManagementMode mode) throws STException {
        switch(mode) {
            case RF_DISABLE:
                this.mRegisterRfMgt.setRFMngt(ST25DVRegisterRFMngtControl.RF_DISABLE, true);
                break;
            case RF_SLEEP:
                this.mRegisterRfMgt.setRFMngt(ST25DVRegisterRFMngtControl.RF_SLEEP, true);
        }

    }

    public void initEmptyCCFile() throws STException {
        super.initEmptyCCFile();
        int area1SizeInBytes = this.getAreaSizeInBytes(1);
        if (area1SizeInBytes <= 512) {
            this.mCCFile.setSupportMultipleBlockReadCommand(false);
        }

    }

    public byte[] getExpectedCCFileData() throws STException {
        byte[] expectedCCFileData = super.getExpectedCCFileData();
        int area1SizeInBytes = this.getAreaSizeInBytes(1);
        if (area1SizeInBytes <= 512) {
            expectedCCFileData[3] = 0;
        }

        return expectedCCFileData;
    }

    static {
        mGpoCommandValue.put(com.st.st25sdk.type5.st25dv.ST25DVTag.GpoCommand.INTERRUPT, (byte) -128);
        mGpoCommandValue.put(com.st.st25sdk.type5.st25dv.ST25DVTag.GpoCommand.SET, (byte)0);
        mGpoCommandValue.put(com.st.st25sdk.type5.st25dv.ST25DVTag.GpoCommand.RESET, (byte)1);
    }

    public static enum RfManagementMode {
        RF_DISABLE,
        RF_SLEEP;

        private RfManagementMode() {
        }
    }

    public static enum GpoCommand {
        INTERRUPT,
        SET,
        RESET;

        private GpoCommand() {
        }
    }
}
