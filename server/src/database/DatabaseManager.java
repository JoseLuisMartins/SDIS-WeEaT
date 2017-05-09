package src.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    //Root path for pgsql folder
    public static String bin_path = "C:\\PostgreSQL\\pg96\\bin";

    public static void setRoot_path(String root_path) {
        DatabaseManager.bin_path = root_path;
    }



    public static void database_create(){

        List<String> commands = get_commands("createdb");

        commands.add("weeat");

        launch_process(commands);

        System.out.println("Create Successfull");
    };

    public static void database_init(){

        List<String> commands = get_commands("psql");

        commands.add("-d");
        commands.add("weeat");
        commands.add("-f");
        commands.add("." + File.separator + "db.sql");

        launch_process(commands);

        System.out.println("Init Successfull");
    };

    public static void database_delete(){

        List<String> commands = get_commands("dropdb");

        commands.add("weeat");

        launch_process(commands);

        System.out.println("Delete Successfull");
    };

    public static void database_backup(){

        List<String> commands = get_commands("pg_dump");

        commands.add("-F");
        commands.add("c");
        commands.add("-v");
        commands.add("-f");
        commands.add("." + File.separator + "db.backup");
        commands.add("weeat");

        launch_process(commands);

        System.out.println("Backup Successfull");
    };

    public static void database_restore(){

        List<String> commands = get_commands("pg_restore");

        commands.add("-d");
        commands.add("weeat");		// nome da database a fazer restore
        commands.add("-v");
        commands.add("." + File.separator + "db.backup"); // local do file output após backup

        launch_process(commands);

        System.out.println("Restore Successfull");
    };

    private static void launch_process(List<String> commands) {

        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.environment().put("PGPASSWORD", "q1w2e3r4t5");
        System.out.println("Running : " + pb.command().toString());

        try {
            Process p = pb.start();


            // Handle de erros
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = buf.readLine();

            while (line != null) {
                System.out.println(line);
                line = buf.readLine();
            }
            buf.close();

            p.waitFor();
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<String> get_commands(String cmd) {

        List<String> res = new ArrayList<String>();

        res.add(bin_path + File.separator + cmd);
        res.add("-h");
        res.add("localhost");
        res.add("-p");
        res.add("5432");
        res.add("-U");
        res.add("postgres");

        return res;
    }
}
