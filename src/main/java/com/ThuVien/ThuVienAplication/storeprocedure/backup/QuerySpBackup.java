package com.ThuVien.ThuVienAplication.storeprocedure.backup;

public class QuerySpBackup {
    public static final String Sp_Danh_sach_cac_database  = "EXECUTE Sp_Danh_sach_cac_database ";

    public static final String SP_Backup_database  = "EXECUTE SP_Backup_database ?,?,?";

    public static final String SP_GetBackupHistory  = "EXECUTE SP_GetBackupHistory ? ";


    public static final String SP_RestoreDatabase  = "EXECUTE SP_RestoreDatabase ?,?,?,?,? ";

    public static final String SP_RestoreDatabase_normal  = "EXECUTE SP_RestoreDatabase_normal ?,?,? ";

    public static final String SP_RestoreDatabase_point_in_time  = "EXECUTE SP_RestoreDatabase ?,?,?,?,? ";


    public static final String SP_RestoreDatabase_Normal_FromPath  = "EXECUTE SP_RestoreDatabase_Normal_FromPath ?,?,? ";

}
