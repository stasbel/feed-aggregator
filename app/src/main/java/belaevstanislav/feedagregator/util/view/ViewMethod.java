package belaevstanislav.feedagregator.util.view;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.widget.TextView;

public class ViewMethod {
    private static class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(URLSpan src) {
            super(src.getURL());
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    private static class Factory extends Spannable.Factory {
        @Override
        public Spannable newSpannable(CharSequence source) {
            return new SpannableNoUnderline(source);
        }
    }

    private static class SpannableNoUnderline extends SpannableString {
        public SpannableNoUnderline(CharSequence source) {
            super(source);
        }

        @Override
        public void setSpan(Object what, int start, int end, int flags) {
            if (what instanceof URLSpan) {
                what = new URLSpanNoUnderline((URLSpan) what);
            }
            super.setSpan(what, start, end, flags);
        }
    }

    public static void linkify(TextView textView, int color) {
        Linkify.addLinks(textView, Linkify.ALL);
        textView.setSpannableFactory(new Factory());
        textView.setLinkTextColor(color);
    }
}
