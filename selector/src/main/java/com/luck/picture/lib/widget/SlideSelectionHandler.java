package com.luck.picture.lib.widget;

import java.util.HashSet;
import java.util.Set;

/**
 * @author：luck
 * @date：2022/1/23 10:40 上午
 * @describe：SlideSelectionProcessor
 */
public class SlideSelectionHandler implements SlideSelectTouchListener.OnAdvancedSlideSelectListener {

    private final ISelectionHandler mSelectionHandler;
    private ISelectionStartFinishedListener mStartFinishedListener;
    private HashSet<Integer> mOriginalSelection;

    /**
     * @param selectionHandler the handler that takes care to handle the selection events
     */
    public SlideSelectionHandler(ISelectionHandler selectionHandler) {
        mSelectionHandler = selectionHandler;
        mStartFinishedListener = null;
    }


    /**
     * @param startFinishedListener a listener that get's notified when the drag selection is started or finished
     * @return this
     */
    public SlideSelectionHandler withStartFinishedListener(ISelectionStartFinishedListener startFinishedListener) {
        mStartFinishedListener = startFinishedListener;
        return this;
    }


    @Override
    public void onSelectionStarted(int start) {
        mOriginalSelection = new HashSet<>();
        Set<Integer> selected = mSelectionHandler.getSelection();
        if (selected != null)
            mOriginalSelection.addAll(selected);
        boolean isFirstSelected = mOriginalSelection.contains(start);
        mSelectionHandler.changeSelection(start, start, !mOriginalSelection.contains(start), true);
        if (mStartFinishedListener != null) {
            mStartFinishedListener.onSelectionStarted(start, isFirstSelected);
        }
    }

    @Override
    public void onSelectionFinished(int end) {
        mOriginalSelection = null;
        if (mStartFinishedListener != null)
            mStartFinishedListener.onSelectionFinished(end);
    }

    @Override
    public void onSelectChange(int start, int end, boolean isSelected) {
        for (int i = start; i <= end; i++) {
            checkedChangeSelection(i, i, isSelected != mOriginalSelection.contains(i));
        }
    }

    private void checkedChangeSelection(int start, int end, boolean newSelectionState) {
        mSelectionHandler.changeSelection(start, end, newSelectionState, false);
    }

    public interface ISelectionHandler {
        /**
         * @return the currently selected items => can be ignored
         */
        Set<Integer> getSelection();

        /**
         * update your adapter and select select/unselect the passed index range, you be get a single for all modes but {@link Mode#Simple} and {@link Mode#FirstItemDependent}
         *
         * @param start             the first item of the range who's selection state changed
         * @param end               the last item of the range who's selection state changed
         * @param isSelected        true, if the range should be selected, false otherwise
         * @param calledFromOnStart true, if it was called from the {@link SlideSelectionHandler#onSelectionStarted(int)} event
         */
        void changeSelection(int start, int end, boolean isSelected, boolean calledFromOnStart);
    }

    public interface ISelectionStartFinishedListener {
        /**
         * @param start                  the item on which the drag selection was started at
         * @param originalSelectionState the original selection state
         */
        void onSelectionStarted(int start, boolean originalSelectionState);

        /**
         * @param end the item on which the drag selection was finished at
         */
        void onSelectionFinished(int end);
    }
}
