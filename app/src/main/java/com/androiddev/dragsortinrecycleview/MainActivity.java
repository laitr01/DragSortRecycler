package com.androiddev.dragsortinrecycleview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView viewList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BookAdapter adapter = new BookAdapter(initBooks());
        DragSortRecycler dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.reopen);
        dragSortRecycler.setItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
            @Override
            public void onItemMovedListener(int from, int to) {
                BookEntity book = adapter.getBookAt(from);
                adapter.removeBookAt(from);
                adapter.addBookTo(to, book);
                adapter.notifyDataSetChanged();
            }
        });
        viewList = (RecyclerView) findViewById(R.id.listView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        viewList.setLayoutManager(layoutManager);
        viewList.setItemAnimator(new DefaultItemAnimator());
        viewList.addItemDecoration(dragSortRecycler);
        viewList.addOnItemTouchListener(dragSortRecycler);
        viewList.addOnScrollListener(dragSortRecycler.getScrollListener());
        viewList.setAdapter(adapter);
    }

    private List<BookEntity> initBooks(){
        List<BookEntity> listBooks = new ArrayList<>();
        listBooks.add(new BookEntity("Bạch Dương", "Bạch Dương"));
        listBooks.add(new BookEntity("Bingo ", "Là một món ăn "));
        listBooks.add(new BookEntity("Leo ", "Leo "));
        listBooks.add(new BookEntity("Oracle ", "Là một tập đoàn công nghệ đa quốc gia."));
        listBooks.add(new BookEntity("The LEGO Movie", "Animation"));
        listBooks.add(new BookEntity("Iron Man", "Action & Adventure"));

        return listBooks;
    }

}
