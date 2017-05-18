package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {

    //Root path for pgsql folder
    public static String bin_path = "/usr/bin";//"C:\\PostgreSQL\\pg96\\bin";
    public static int interval = 15;
    private static boolean outdated = false;
    private static boolean backup_started = false;
    private static boolean midnight_delete_started = false;

    public static boolean isOutdated() {
        synchronized (DatabaseManager.class) {
            return outdated;
        }
    }

    public static void setOutdated(boolean outdated) {
        synchronized (DatabaseManager.class) {
            DatabaseManager.outdated = outdated;
        }
    }

    public static void setRoot_path(String root_path) {
        DatabaseManager.bin_path = root_path;
    }

    public static void start_backup() {


        System.out.println("Starting database backup protocol!");

        if(backup_started)
            return;

        backup_started = true;

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if(DatabaseManager.isOutdated()) {
                    DatabaseManager.database_backup();
                    DatabaseManager.setOutdated(false);
                }
            }

        }, 0, interval * 1000);
    }

    public static void start_midnight_delete() {

        System.out.println("Starting midnight clean up protocol!");
        if(midnight_delete_started)
            return;

        midnight_delete_started = true;

        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("UTC");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext0 ;
        zonedNext0 = zonedNow.withHour(0).withMinute(0).withSecond(0);
        if(zonedNow.compareTo(zonedNext0) > 0)
            zonedNext0 = zonedNext0.plusDays(1);

        Duration duration = Duration.between(zonedNow, zonedNext0);
        long initalDelay = duration.getSeconds();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new DeleteTask(), initalDelay,
                24*60*60, TimeUnit.SECONDS);

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
        commands.add("." + File.separator + "received.backup"); // local do file output após backup

        launch_process(commands);

        System.out.println("Restore Successfull");
    };

    private static void launch_process(List<String> commands) {

        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.environment().put("PGPASSWORD", "q1w2e3r4t5");
        System.out.println("Running : " + pb.command().toString());

        try {
            Process p = pb.start();
/*
            // Handle de erros
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = buf.readLine();

            while (line != null) {
                System.out.println(line);
                line = buf.readLine();
            }
            buf.close();
*/
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
