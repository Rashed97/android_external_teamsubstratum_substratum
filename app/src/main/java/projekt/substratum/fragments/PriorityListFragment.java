package projekt.substratum.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.thesurix.gesturerecycler.GestureAdapter;
import com.thesurix.gesturerecycler.GestureManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import projekt.substratum.R;
import projekt.substratum.adapters.PrioritiesAdapter;
import projekt.substratum.config.References;
import projekt.substratum.model.Priorities;
import projekt.substratum.model.PrioritiesItem;

import static projekt.substratum.config.References.REFRESH_WINDOW_DELAY;

public class PriorityListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.priority_list_fragment,
                container, false);
        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        final FloatingActionButton applyFab = (FloatingActionButton) root.findViewById(R.id
                .profile_apply_fab);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        final ProgressBar headerProgress = (ProgressBar) root.findViewById(R.id
                .priority_header_loading_bar);
        headerProgress.setVisibility(View.GONE);

        applyFab.hide();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.action_bar_toolbar);
        toolbar.setTitle(getString(R.string.priority_back_title));
        toolbar.setNavigationIcon(getContext().getDrawable(R.drawable.priorities_back_button));
        toolbar.setNavigationOnClickListener(v -> {
            Fragment fragment = new PriorityLoaderFragment();
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim
                    .slide_out_right);
            transaction.replace(R.id.main, fragment);
            transaction.commit();
        });

        // Begin loading up list
        String obtained_key = "";
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            obtained_key = bundle.getString("package_name", null);
        }

        final List<PrioritiesItem> prioritiesList = new ArrayList<>();
        final ArrayList<String> workable_list = new ArrayList<>();
        Process nativeApp = null;
        try {
            nativeApp = Runtime.getRuntime().exec(References.listAllOverlays());

            try (OutputStream stdin = nativeApp.getOutputStream();
                 InputStream stderr = nativeApp.getErrorStream();
                 InputStream stdout = nativeApp.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(stdout))) {
                String line;

                stdin.write(("ls\n").getBytes());
                stdin.write("exit\n".getBytes());

                String current_header = "";
                while ((line = br.readLine()) != null) {
                    if (line.length() > 0) {
                        if (line.equals(obtained_key)) {
                            current_header = line;
                        } else {
                            if (current_header.equals(obtained_key)) {
                                if (line.contains("[x]")) {
                                    int version = References.checkOMSVersion(getContext());
                                    if (version == 3) {
                                        prioritiesList.add(new Priorities(line.substring(8),
                                                References.grabAppIcon(getContext(),
                                                        current_header)));
                                        workable_list.add(line.substring(8));
                                    } else if (version == 7) {
                                        prioritiesList.add(new Priorities(line.substring(4),
                                                References.grabAppIcon(getContext(),
                                                        current_header)));
                                        workable_list.add(line.substring(4));
                                    }
                                } else if (!line.contains("[ ]")) {
                                    break;
                                }
                            }
                        }
                    }
                }

                try (BufferedReader br1 = new BufferedReader(new InputStreamReader(stderr))) {
                    while ((line = br1.readLine()) != null) {
                        Log.e("PriorityListFragment", line);
                    }
                }
            }
        } catch (IOException ioe) {
            Log.e("PriorityListFragment", "There was an issue regarding loading the priorities of" +
                    " " +
                    "each overlay.");
        } finally {
            if (nativeApp != null) {
                // destroy the process explicitly
                nativeApp.destroy();
            }
        }

        final PrioritiesAdapter adapter = new PrioritiesAdapter(getContext(), R.layout.linear_item);
        adapter.setData(prioritiesList);

        recyclerView.setAdapter(adapter);

        new GestureManager.Builder(recyclerView)
                .setManualDragEnabled(true)
                .setGestureFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.UP
                        | ItemTouchHelper.DOWN)
                .build();

        adapter.setDataChangeListener(new GestureAdapter.OnDataChangeListener<PrioritiesItem>() {
            @Override
            public void onItemRemoved(final PrioritiesItem item, final int position) {
            }

            @Override
            public void onItemReorder(final PrioritiesItem item, final int fromPos, final int
                    toPos) {
                /*
                ==========================================================================
                A detailed explanation of the OMS "om set-priority PACKAGE PARENT" command
                ==========================================================================

                1. The command accepts two variables, PACKAGE and PARENT.

                2. PARENT can also be "highest" or "lowest" to ensure it is on top of the list

                3. When you specify a PACKAGE (e.g. com.android.systemui.Beltz), you want to shift
                it HIGHER than the parent.

                4. The PARENT will always be a specified value that will be an index below the final
                result of PACKAGE, for example (om set-priority com.android.systemui.Beltz
                com.android.systemui.Domination)

                5. com.android.systemui.Beltz will be a HIGHER priority than
                com.android.systemui.Domination

                */

                if (fromPos != toPos) {
                    String move_package = workable_list.get(fromPos);
                    // As workable list is a simulation of the priority list without object
                    // values, we have to simulate the events such as adding above parents
                    workable_list.remove(fromPos);
                    workable_list.add(toPos, move_package);
                    applyFab.show();
                }
            }
        });

        applyFab.setOnClickListener(v -> {
            applyFab.hide();
            if (getView() != null) {
                Snackbar.make(getView(),
                        getString(R.string.
                                priority_success_toast),
                        Snackbar.LENGTH_INDEFINITE)
                        .show();
            }
            headerProgress.setVisibility(View.VISIBLE);
            References.setPriority(getContext(), workable_list);
            if (References.checkOMSVersion(getContext()) == 7 &&
                    !workable_list.contains("projekt.substratum")) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    // OMS may not have written all the changes so
                    // quickly just yet so we may need to have a small delay
                    try {
                        getActivity().recreate();
                    } catch (Exception e) {
                        // Consume window refresh
                    }
                }, REFRESH_WINDOW_DELAY * 2);
            }
        });
        return root;
    }
}