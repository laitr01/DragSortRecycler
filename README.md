# How to use it?

![alt text](https://media.giphy.com/media/26Ff5zOO60eOubufu/giphy.gif)

-----------------------------------------------------------------------------------------------------------
Sample												-------------------------------------------------------
-----------------------------------------------------------------------------------------------------------

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
		
-----------------------------------------------------------------------------------------------------------