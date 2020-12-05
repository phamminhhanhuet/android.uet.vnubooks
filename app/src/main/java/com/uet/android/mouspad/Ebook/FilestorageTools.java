package com.uet.android.mouspad.Ebook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;


import com.uet.android.mouspad.R;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FilestorageTools {

    private final Context mContext;

    public FilestorageTools(Context context) {
        mContext = context;
    }

    private Map<File,String> getDrives() {
        List<File> roots = new ArrayList<>();
        roots.add(new File("/storage"));
        try {
            roots.addAll(Arrays.asList(mContext.getExternalFilesDirs(null)));
        } catch (Throwable t) {
            Log.e("storage", t.getMessage(), t);
        }

        Map<File,String> files = new LinkedHashMap<>();
        for(File r: roots) {
            try {
                int sd = 1;
                if (r!=null) {
                    for (File e : r.listFiles()) {
                        try {
                            Log.d("storage", e.getPath() + " " + e.isDirectory());
                            if (e.isDirectory()) { 
                                boolean removable = false;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    removable = Environment.isExternalStorageRemovable(e);
                                }
                                String name = "SD";
                                if (sd++ > 1) name += sd;
                                files.put(e, removable ? name : e.getName());
                            }
                        } catch (IllegalArgumentException ex) {
                            Log.d("storage", e.getPath() + " is no good");
                        } catch (Throwable t) {
                            Log.e("storage", t.getMessage(), t);
                        }

                    }
                }
            } catch (Exception e) {
                Log.e("storage", e.getMessage(), e);
            }
        }

        File ext = Environment.getExternalStorageDirectory();
        if (Environment.isExternalStorageEmulated()) {
            files.put(ext, "Internal");
        } else if (Environment.isExternalStorageRemovable()) {
            files.put(ext, "SD");
        }

        return files;
    }

    //list direction in direction
    private Map<File,String> listDirsInDir(File extdir, final String matchRE, final boolean onlyDirs) {
        Map<File,String> allfiles = new LinkedHashMap<>();

        Map<File,String> drives = getDrives();

        if (extdir==null) {
            return drives;
        }

        List<File> dirs;
        if (!extdir.exists() || !extdir.isDirectory()) {
            dirs = new ArrayList<>();
        } else {
            FilenameFilter filterdirs = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return sel.isDirectory();
                }

            };

            try {
                dirs = new ArrayList<>(Arrays.asList(extdir.listFiles(filterdirs)));
            } catch (Exception e) {
                Log.d("FilestorageTools", e.getMessage(), e);
                dirs = new ArrayList<>();
            }
        }

        Collections.sort(dirs, new Comparator<File>() {
            @Override
            public int compare(File s, File t1) {
                return s.getName().compareToIgnoreCase(t1.getName());
            }
        });

        if (extdir.getParent()!=null) {
            if (drives.keySet().contains(extdir)) {
                allfiles.put(null, "← ..");
            } else {
                allfiles.put(extdir.getParentFile(), "← ..");
            }
        }

        for (File dir: dirs) {
            allfiles.put(dir, "\uD83D\uDCC1 " + dir.getName());
        }


        if (!onlyDirs) {
            FilenameFilter filterfiles = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return sel.isFile() && (matchRE == null || filename.matches(matchRE));
                }

            };

            List<File> files;
            if (!extdir.exists() || !extdir.isDirectory()) {
                files = new ArrayList<>();
            } else {
                try {
                    files = new ArrayList<>(Arrays.asList(extdir.listFiles(filterfiles)));
                } catch (Exception e) {
                    Log.d("FilestorageTools", e.getMessage(), e);
                    files = new ArrayList<>();
                }
            }

            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File s, File t1) {
                    return s.getName().compareToIgnoreCase(t1.getName());
                }
            });

            for(File file: files) {
                allfiles.put(file, "\uD83D\uDCC4 " + file.getName());
            }
        }

        return allfiles;

    }

    public void selectExternalLocation(final SelectionMadeListener listener, String title, boolean chooseDir) {
        selectExternalLocation(listener, title, null, chooseDir, null);
    }

    public void selectExternalLocation(final SelectionMadeListener listener, String title, boolean chooseDir, String matchRE) {
        selectExternalLocation(listener, title, null, chooseDir, matchRE);
    }

//hien thi builder chon folder
    public void selectExternalLocation(final SelectionMadeListener listener, final String title, final String startdir, final boolean chooseDir, final String matchRE) {

        String dname = startdir==null ? "" : startdir;

        Map<File,String> listDirsInDir = listDirsInDir(startdir==null?null:new File(startdir), matchRE, chooseDir);

        final File [] fileItems = new File[listDirsInDir.size()];
        final String [] showItems  = new String[listDirsInDir.size()];

        int i=0;
        for (Map.Entry<File,String> entry: listDirsInDir.entrySet()) {
            fileItems[i] = entry.getKey();
            showItems[i] = entry.getValue();
            i++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(title + "\n" + dname);

        builder.setItems(showItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                try {
                    File selFile = fileItems[i];
                    if (selFile==null) {
                        selectExternalLocation(listener, title, null, chooseDir, matchRE);
                    } else if (selFile.isDirectory()) {
                        selectExternalLocation(listener, title, selFile.getPath(), chooseDir, matchRE);
                    } else {
                        listener.selected(selFile.getCanonicalFile());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        if (chooseDir && startdir!=null) {
            builder.setPositiveButton(R.string.select_thisfolder, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    listener.selected(new File(startdir));
                }
            });
        }
        builder.setNegativeButton(R.string.cancel, null);

        builder.show();
    }


    public interface SelectionMadeListener {
        void selected(File selection);
    }

    public static boolean deleteDir(File dir) {

        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDir(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return dir.delete();
    }
}
