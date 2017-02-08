package projekt.substratum.util;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import projekt.substratum.R;
import projekt.substratum.config.References;

public class SoundsHandler {

    private static final String SYSTEM_MEDIA_PATH = "/system/media/audio";
    private static final String SYSTEM_ALARMS_PATH =
            SYSTEM_MEDIA_PATH + File.separator + "alarms";
    private static final String SYSTEM_RINGTONES_PATH =
            SYSTEM_MEDIA_PATH + File.separator + "ringtones";
    private static final String SYSTEM_NOTIFICATIONS_PATH =
            SYSTEM_MEDIA_PATH + File.separator + "notifications";
    private static final String MEDIA_CONTENT_URI = "content://media/internal/audio/media";
    private static final String SYSTEM_CONTENT_URI = "content://settings/global";

    private Context mContext;
    private ProgressDialog progress;
    private String theme_pid;
    private boolean has_failed;
    private boolean ringtone = false;
    private SharedPreferences prefs;
    private View view;

    public void execute(View view, String arguments, Context context, String theme_pid) {
        this.mContext = context;
        this.theme_pid = theme_pid;
        this.view = view;
        new SoundsHandlerAsync().execute(arguments);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void SoundsClearer(Context context) {
        References.clearSounds(context);
    }

    private void perform_action() {
        // Let's start with user interface sounds
        File ui = new File(mContext.getCacheDir().getAbsolutePath() +
                "/SoundsCache/SoundsInjector/ui/");
        File ui_temp = new File("/data/system/theme/audio/ui/");
        if (ui_temp.exists()) {
            References.delete("/data/system/theme/audio/ui/");
        }
        if (ui.exists()) {
            References.createNewFolder("/data/system/theme/audio/ui/");

            File effect_tick_mp3 = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/ui/Effect_Tick.mp3");
            File effect_tick_ogg = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/ui/Effect_Tick.ogg");
            if (effect_tick_mp3.exists() || effect_tick_ogg.exists()) {
                boolean mp3 = effect_tick_mp3.exists();
                boolean ogg = effect_tick_ogg.exists();
                if (mp3) {
                    References.copyDir(mContext.getCacheDir().getAbsolutePath() +
                                    "/SoundsCache/SoundsInjector/ui/Effect_Tick.mp3",
                            "/data/system/theme/audio/ui/Effect_Tick.mp3");
                    setUIAudible(mContext, effect_tick_mp3, new File
                            ("/data/system/theme/audio/ui/Effect_Tick.mp3"), RingtoneManager
                            .TYPE_RINGTONE, "Effect_Tick");
                }
                if (ogg) {
                    References.copyDir(mContext.getCacheDir().getAbsolutePath() +
                                    "/SoundsCache/SoundsInjector/ui/Effect_Tick.ogg",
                            "/data/system/theme/audio/ui/Effect_Tick.ogg");
                    setUIAudible(mContext, effect_tick_ogg, new File
                            ("/data/system/theme/audio/ui/Effect_Tick.ogg"), RingtoneManager
                            .TYPE_RINGTONE, "Effect_Tick");
                }
            } else {
                setDefaultUISounds("lock_sound", "Lock.ogg");
            }

            File new_lock_mp3 = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/ui/Lock.mp3");
            File new_lock_ogg = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/ui/Lock.ogg");
            if (new_lock_mp3.exists() || new_lock_ogg.exists()) {
                boolean mp3 = new_lock_mp3.exists();
                boolean ogg = new_lock_ogg.exists();
                if (mp3) {
                    References.move(mContext.getCacheDir().getAbsolutePath() +
                                    "/SoundsCache/SoundsInjector/ui/Lock.mp3",
                            "/data/system/theme/audio/ui/Lock.mp3");
                    setUISounds("lock_sound", "/data/system/theme/audio/ui/Lock.mp3");
                }
                if (ogg) {
                    References.move(mContext.getCacheDir().getAbsolutePath() +
                                    "/SoundsCache/SoundsInjector/ui/Lock.ogg",
                            "/data/system/theme/audio/ui/Lock.ogg");
                    setUISounds("lock_sound", "/data/system/theme/audio/ui/Lock.ogg");
                }
            } else {
                setDefaultUISounds("lock_sound", "Lock.ogg");
            }

            File new_unlock_mp3 = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/ui/Unlock.mp3");
            File new_unlock_ogg = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/ui/Unlock.ogg");
            if (new_unlock_mp3.exists() || new_unlock_ogg.exists()) {
                boolean mp3 = new_unlock_mp3.exists();
                boolean ogg = new_unlock_ogg.exists();
                if (mp3) {
                    References.move(mContext.getCacheDir().getAbsolutePath() +
                                    "/SoundsCache/SoundsInjector/ui/Unlock.mp3",
                            "/data/system/theme/audio/ui/Unlock.mp3");
                    setUISounds("unlock_sound", "/data/system/theme/audio/ui/Unlock.mp3");
                }
                if (ogg) {
                    References.move(mContext.getCacheDir().getAbsolutePath() +
                                    "/SoundsCache/SoundsInjector/ui/Unlock.ogg",
                            "/data/system/theme/audio/ui/Unlock.ogg");
                    setUISounds("unlock_sound", "/data/system/theme/audio/ui/Unlock.ogg");
                }
            } else {
                setDefaultUISounds("unlock_sound", "Unlock.ogg");
            }

            File new_lowbattery_mp3 = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/ui/LowBattery.mp3");
            File new_lowbattery_ogg = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/ui/LowBattery.ogg");
            if (new_lowbattery_mp3.exists() || new_lowbattery_ogg.exists()) {
                boolean mp3 = new_lowbattery_mp3.exists();
                boolean ogg = new_lowbattery_ogg.exists();
                if (mp3) {
                    References.move(mContext.getCacheDir().getAbsolutePath() +
                                    "/SoundsCache/SoundsInjector/ui/LowBattery.mp3",
                            "/data/system/theme/audio/ui/LowBattery.mp3");
                    setUISounds("low_battery_sound",
                            "/data/system/theme/audio/ui/LowBattery.mp3");
                }
                if (ogg) {
                    References.move(mContext.getCacheDir().getAbsolutePath() +
                                    "/SoundsCache/SoundsInjector/ui/LowBattery.ogg",
                            "/data/system/theme/audio/ui/LowBattery.ogg");
                    setUISounds("low_battery_sound",
                            "/data/system/theme/audio/ui/LowBattery.ogg");
                }
            } else {
                setDefaultUISounds("low_battery_sound", "LowBattery.ogg");
            }
            References.setPermissionsRecursively(644, "/data/system/theme/audio/ui/");
            References.setPermissions(755, "/data/system/theme/audio/ui/");
            References.setPermissions(755, "/data/system/theme/audio/");
            References.setPermissions(755, "/data/system/theme/");
            References.setContext("/data/system/theme");
        }

        // Now let's set the common user's sound files found in Settings
        File alarms = new File(mContext.getCacheDir().getAbsolutePath() +
                "/SoundsCache/SoundsInjector/alarms/");
        File alarms_temp = new File("/data/system/theme/audio/alarms/");
        if (alarms_temp.exists()) References.delete("/data/system/theme/audio/alarms/");
        if (alarms.exists()) {
            File new_alarm_mp3 = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/alarms/" + "/alarm.mp3");
            File new_alarm_ogg = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/alarms/" + "/alarm.ogg");
            if (new_alarm_mp3.exists() || new_alarm_ogg.exists()) {
                boolean mp3 = new_alarm_mp3.exists();
                boolean ogg = new_alarm_ogg.exists();

                References.copyDir(mContext.getCacheDir().getAbsolutePath() +
                                "/SoundsCache/SoundsInjector/alarms/",
                        "/data/system/theme/audio/");
                References.setPermissionsRecursively(644, "/data/system/theme/audio/alarms/");
                References.setPermissions(755, "/data/system/theme/audio/alarms/");

                // Prior to setting, we should clear out the current ones
                clearAudibles(mContext, "/data/system/theme/audio/alarms/alarm.mp3");
                clearAudibles(mContext, "/data/system/theme/audio/alarms/alarm.ogg");

                if (mp3)
                    setAudible(mContext, new File("/data/system/theme/audio/alarms/alarm.mp3"),
                            new File(alarms.getAbsolutePath(), "alarm.mp3"),
                            RingtoneManager.TYPE_ALARM,
                            mContext.getString(R.string.content_resolver_alarm_metadata));
                if (ogg)
                    setAudible(mContext, new File("/data/system/theme/audio/alarms/alarm.ogg"),
                            new File(alarms.getAbsolutePath(), "alarm.ogg"),
                            RingtoneManager.TYPE_ALARM,
                            mContext.getString(R.string.content_resolver_alarm_metadata));
            } else {
                setDefaultAudible(mContext, RingtoneManager.TYPE_ALARM);
            }
        }


        File notifications = new File(mContext.getCacheDir().getAbsolutePath() +
                "/SoundsCache/SoundsInjector/notifications/");
        File notifications_temp = new File("/data/system/theme/audio/notifications/");
        if (notifications_temp.exists())
            References.delete("/data/system/theme/audio/notifications/");
        if (notifications.exists()) {
            ringtone = true;
            File new_notifications_mp3 = new File(mContext.getCacheDir()
                    .getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/notifications/" + "/notification.mp3");
            File new_notifications_ogg = new File(mContext.getCacheDir()
                    .getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/notifications/" + "/notification.ogg");
            if (new_notifications_mp3.exists() || new_notifications_ogg.exists()) {
                boolean mp3 = new_notifications_mp3.exists();
                boolean ogg = new_notifications_ogg.exists();

                References.copyDir(mContext.getCacheDir().getAbsolutePath() +
                                "/SoundsCache/SoundsInjector/notifications/",
                        "/data/system/theme/audio/");
                References.setPermissionsRecursively(644,
                        "/data/system/theme/audio/notifications/");
                References.setPermissions(755, "/data/system/theme/audio/notifications/");

                // Prior to setting, we should clear out the current ones
                clearAudibles(mContext, "/data/system/theme/audio/notifications/notification.mp3");
                clearAudibles(mContext, "/data/system/theme/audio/notifications/notification.ogg");

                if (mp3)
                    setAudible(mContext, new File
                                    ("/data/system/theme/audio/notifications/notification.mp3"),
                            new File(notifications.getAbsolutePath(), "notification.mp3"),
                            RingtoneManager.TYPE_NOTIFICATION,
                            mContext.getString(R.string.content_resolver_notification_metadata));
                if (ogg)
                    setAudible(mContext, new File
                                    ("/data/system/theme/audio/notifications/notification.ogg"),
                            new File(notifications.getAbsolutePath(), "notification.ogg"),
                            RingtoneManager.TYPE_NOTIFICATION,
                            mContext.getString(R.string.content_resolver_notification_metadata));
            } else {
                setDefaultAudible(mContext, RingtoneManager.TYPE_NOTIFICATION);
            }
        } else {
            ringtone = false;
        }

        File ringtones = new File(mContext.getCacheDir().getAbsolutePath() +
                "/SoundsCache/SoundsInjector/ringtones/");
        File ringtones_temp = new File("/data/system/theme/audio/ringtones/");
        if (ringtones_temp.exists())
            References.delete("/data/system/theme/audio/ringtones/");
        if (ringtones.exists()) {
            ringtone = true;
            File new_ringtones_mp3 = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/ringtones/" + "/ringtone.mp3");
            File new_ringtones_ogg = new File(mContext.getCacheDir().getAbsolutePath() +
                    "/SoundsCache/SoundsInjector/ringtones/" + "/ringtone.ogg");
            if (new_ringtones_mp3.exists() || new_ringtones_ogg.exists()) {
                boolean mp3 = new_ringtones_mp3.exists();
                boolean ogg = new_ringtones_ogg.exists();

                References.copyDir(mContext.getCacheDir().getAbsolutePath() +
                                "/SoundsCache/SoundsInjector/ringtones/",
                        "/data/system/theme/audio/");
                References.setPermissionsRecursively(644, "/data/system/theme/audio/ringtones/");
                References.setPermissions(755, "/data/system/theme/audio/ringtones/");

                // Prior to setting, we should clear out the current ones
                clearAudibles(mContext, "/data/system/theme/audio/ringtones/ringtone.mp3");
                clearAudibles(mContext, "/data/system/theme/audio/ringtones/ringtone.ogg");

                if (mp3)
                    setAudible(mContext, new File
                                    ("/data/system/theme/audio/ringtones/ringtone.mp3"),
                            new File(ringtones.getAbsolutePath(), "ringtone.mp3"),
                            RingtoneManager.TYPE_RINGTONE,
                            mContext.getString(R.string.content_resolver_ringtone_metadata));
                if (ogg)
                    setAudible(mContext, new File
                                    ("/data/system/theme/audio/ringtones/ringtone.ogg"),
                            new File(ringtones.getAbsolutePath(), "ringtone.ogg"),
                            RingtoneManager.TYPE_RINGTONE,
                            mContext.getString(R.string.content_resolver_ringtone_metadata));
            } else {
                setDefaultAudible(mContext, RingtoneManager.TYPE_RINGTONE);
            }
        } else {
            ringtone = false;
        }
    }

    private String getDefaultAudiblePath(int type) {
        final String name;
        final String path;
        switch (type) {
            case RingtoneManager.TYPE_ALARM:
                name = getProp("ro.config.alarm_alert");
                path = name != null ? SYSTEM_ALARMS_PATH + File.separator + name : null;
                break;
            case RingtoneManager.TYPE_NOTIFICATION:
                name = getProp("ro.config.notification_sound");
                path = name != null ? SYSTEM_NOTIFICATIONS_PATH + File.separator + name : null;
                break;
            case RingtoneManager.TYPE_RINGTONE:
                name = getProp("ro.config.ringtone");
                path = name != null ? SYSTEM_RINGTONES_PATH + File.separator + name : null;
                break;
            default:
                path = null;
                break;
        }
        return path;
    }

    private String getProp(String propName) {
        Process p = null;
        String result = "";
        try {
            p = new ProcessBuilder("/system/bin/getprop",
                    propName).redirectErrorStream(true).start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream())
            )) {
                String line;
                while ((line = br.readLine()) != null) {
                    result = line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return result;
    }

    private boolean setUISounds(String sound_name, String location) {
        if (References.allowedUISound(sound_name)) {
            References.adjustContentProvider(SYSTEM_CONTENT_URI, sound_name, location);
            return true;
        }
        return false;
    }

    private void setDefaultUISounds(String sound_name, String sound_file) {
        References.adjustContentProvider(SYSTEM_CONTENT_URI, sound_name,
                "/system/media/audio/ui/" + sound_file);
    }

    private boolean setAudible(Context context, File ringtone, File ringtoneCache, int type,
                               String name) {
        final String path = ringtone.getAbsolutePath();
        final String mimeType = name.endsWith(".ogg") ? "application/ogg" : "application/mp3";
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, path);
        values.put(MediaStore.MediaColumns.TITLE, name);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.SIZE, ringtoneCache.length());
        values.put(MediaStore.Audio.Media.IS_RINGTONE, type == RingtoneManager.TYPE_RINGTONE);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION,
                type == RingtoneManager.TYPE_NOTIFICATION);
        values.put(MediaStore.Audio.Media.IS_ALARM, type == RingtoneManager.TYPE_ALARM);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
        Uri newUri = null;
        Cursor c = context.getContentResolver().query(uri,
                new String[]{MediaStore.MediaColumns._ID},
                MediaStore.MediaColumns.DATA + "='" + path + "'",
                null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            long id = c.getLong(0);
            c.close();
            newUri = Uri.withAppendedPath(Uri.parse(MEDIA_CONTENT_URI), "" + id);
            context.getContentResolver().update(uri, values,
                    MediaStore.MediaColumns._ID + "=" + id, null);
        }
        if (newUri == null)
            newUri = context.getContentResolver().insert(uri, values);
        try {
            RingtoneManager.setActualDefaultRingtoneUri(context, type, newUri);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean setUIAudible(Context context, File localized_ringtone,
                                 File ringtone_file, int type, String name) {
        final String path = ringtone_file.getAbsolutePath();

        final String path_clone = "/system/media/audio/ui/" + name + ".ogg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, path);
        values.put(MediaStore.MediaColumns.TITLE, name);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/ogg");
        values.put(MediaStore.MediaColumns.SIZE, localized_ringtone.length());
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
        Uri newUri = null;
        Cursor c = context.getContentResolver().query(uri,
                new String[]{MediaStore.MediaColumns._ID},
                MediaStore.MediaColumns.DATA + "='" + path_clone + "'",
                null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            long id = c.getLong(0);
            Log.e("ContentResolver", id + "");
            c.close();
            newUri = Uri.withAppendedPath(Uri.parse(MEDIA_CONTENT_URI), "" + id);
            try {
                context.getContentResolver().update(uri, values,
                        MediaStore.MediaColumns._ID + "=" + id, null);
            } catch (Exception e) {
                Log.d("SoundsHandler", "The content provider does not need to be updated.");
            }
        }
        if (newUri == null)
            newUri = context.getContentResolver().insert(uri, values);
        try {
            RingtoneManager.setActualDefaultRingtoneUri(context, type, newUri);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean setDefaultAudible(Context context, int type) {
        final String audiblePath = getDefaultAudiblePath(type);
        if (audiblePath != null) {
            Uri uri = MediaStore.Audio.Media.getContentUriForPath(audiblePath);
            Cursor c = context.getContentResolver().query(uri,
                    new String[]{MediaStore.MediaColumns._ID},
                    MediaStore.MediaColumns.DATA + "='" + audiblePath + "'",
                    null, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                long id = c.getLong(0);
                c.close();
                uri = Uri.withAppendedPath(
                        Uri.parse(MEDIA_CONTENT_URI), "" + id);
            }
            if (uri != null)
                RingtoneManager.setActualDefaultRingtoneUri(context, type, uri);
        } else {
            return false;
        }
        return true;
    }

    private void clearAudibles(Context context, String audiblePath) {
        final File audibleDir = new File(audiblePath);
        if (audibleDir.exists() && audibleDir.isDirectory()) {
            String[] files = audibleDir.list();
            final ContentResolver resolver = context.getContentResolver();
            for (String s : files) {
                final String filePath = audiblePath + File.separator + s;
                Uri uri = MediaStore.Audio.Media.getContentUriForPath(filePath);
                resolver.delete(uri, MediaStore.MediaColumns.DATA + "=\""
                        + filePath + "\"", null);
                boolean deleted = (new File(filePath)).delete();
                if (deleted) Log.e("SoundsHandler", "Database cleared");
            }
        }
    }

    private class SoundsHandlerAsync extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            try {
                progress = new ProgressDialog(mContext, R.style.AppTheme_DialogAlert);
                progress.setMessage(mContext.getString(R.string.sounds_dialog_apply_text));
                progress.setIndeterminate(false);
                progress.setCancelable(false);
                progress.show();
            } catch (Exception e) {
                e.printStackTrace();
                // Suppress warning
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            if (!has_failed) {
                Snackbar.make(view,
                        mContext.getString(R.string.sounds_dialog_apply_success),
                        Snackbar.LENGTH_LONG)
                        .show();
            } else {
                Snackbar.make(view,
                        mContext.getString(R.string.sounds_dialog_apply_failed),
                        Snackbar.LENGTH_LONG)
                        .show();
            }
            References.mountROData();
            References.mountRO();

            if (ringtone) {
                ringtone = false;
                if (!Settings.System.canWrite(mContext)) {
                    new AlertDialog.Builder(mContext)
                            .setTitle(mContext.getString(R.string.sounds_dialog_permissions_title))
                            .setMessage(mContext.getString(R.string.sounds_dialog_permissions_text))
                            .setPositiveButton(R.string.sounds_dialog_permissions_grant,
                                    (dialog, which) -> {
                                        if (!Settings.System.canWrite(mContext)) {
                                            Intent intent = new Intent(
                                                    Settings
                                                            .ACTION_MANAGE_WRITE_SETTINGS);
                                            intent.setData(Uri.parse("package:" +
                                                    mContext.getPackageName()));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            mContext.startActivity(intent);
                                        } else {
                                            Log.d(References.SUBSTRATUM_LOG,
                                                    "Substratum was granted " +
                                                            "'android.permission" +
                                                            ".WRITE_SETTINGS' " +
                                                            "permissions for system " +
                                                            "runtime code " +
                                                            "execution.");
                                        }
                                    })
                            .setNegativeButton(R.string.sounds_dialog_permissions_deny,
                                    (dialog, which) -> dialog.dismiss())
                            .setIcon(mContext.getDrawable(R.drawable.sounds_dialog_alert))
                            .show();
                }
            }
        }

        @Override
        protected String doInBackground(String... sUrl) {

            boolean[] results = References.setSounds(mContext, theme_pid, sUrl[0]);
            has_failed = results[0];
            ringtone = results[1];

            if (!has_failed) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("sounds_applied", theme_pid);
                editor.apply();
                Log.d("SoundsHandler", "Sound pack installed!");
                References.delete(mContext.getCacheDir().getAbsolutePath() +
                        "/SoundsCache/SoundsInjector/");
            } else {
                Log.e("SoundsHandler", "Sound installation aborted!");
                References.delete(mContext.getCacheDir().getAbsolutePath() +
                        "/SoundsCache/SoundsInjector/");
            }

            return null;
        }

        private void CopyStream(InputStream Input, OutputStream Output) throws IOException {
            byte[] buffer = new byte[5120];
            int length = Input.read(buffer);
            while (length > 0) {
                Output.write(buffer, 0, length);
                length = Input.read(buffer);
            }
        }

        private void unzip(String source, String destination) {
            try (ZipInputStream inputStream = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(source)))) {
                ZipEntry zipEntry;
                int count;
                byte[] buffer = new byte[8192];
                while ((zipEntry = inputStream.getNextEntry()) != null) {
                    File file = new File(destination, zipEntry.getName());
                    File dir = zipEntry.isDirectory() ? file : file.getParentFile();
                    if (!dir.isDirectory() && !dir.mkdirs())
                        throw new FileNotFoundException("Failed to ensure directory: " +
                                dir.getAbsolutePath());
                    if (zipEntry.isDirectory())
                        continue;
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        while ((count = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, count);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SoundsHandler",
                        "An issue has occurred while attempting to decompress this archive.");
            }
        }
    }
}