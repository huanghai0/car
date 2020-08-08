package com.example.car.common;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FindArrayList<T> {

    private final Class<T> type;

    public FindArrayList(Class<T> type) {
        this.type = type;
    }

    public Class<T> getMyType() {
        return this.type;
    }

    /**
     *
     * @param callable
     * @return List<Future> 转 ArrayList<T>
     * @throws InterruptedException
     * @throws ExecutionException
     */

    public ArrayList<T> findList(Callable callable) throws InterruptedException, ExecutionException {

        ArrayList<T> arrlist = new ArrayList<>();

        GetMyFutureList getMyFutureList = new GetMyFutureList();
        List<Future> list = getMyFutureList.getObList(callable);
        for (Future ff : list) {
            String str = ff.get().toString();
            JsonToList<T> jsonToList = new JsonToList<T>(getMyType());
            arrlist = jsonToList.jtl(str);
        }

        return arrlist;
    }

}
