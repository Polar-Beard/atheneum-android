package com.satchlapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sara on 1/26/2017.
 */
public class HtmlTagPositioner {


    private String text;
    private int[] positions;
    private List<HtmlTag> htmlTags;

    public HtmlTagPositioner() {
        htmlTags = new ArrayList<>();
    }

    public void setText(String text) {

    }

    public String getText() {
        return text;
    }

    private void insertHtmlIntoText(){
        for(HtmlTag tag: htmlTags){
            String core = "<" + tag.getTag() + ">"
                    + text.substring(tag.getStartPosition(), tag.getEndPosition())
                    + "</" + tag.getTag() + ">";

            if (tag.getStartPosition() != 0) {
                core = text.substring(0, tag.getStartPosition()) + core;
            }
            if (tag.getEndPosition() != text.length()) {
                core = core + text.substring(tag.getEndPosition(), text.length());
            }
        }
    }

    public void addHtmlTag(HtmlTag.Type type) {
        HtmlTag htmlTag = new HtmlTag(type);
        String core = "<" + htmlTag.getTag() + ">" + text.substring(positions[0], positions[1])
                + "</" + htmlTag.getTag() + ">";
        htmlTag.setStartPosition(positions[0]);
        htmlTag.setLength(core.length());
        htmlTags.add(htmlTag);

        if (positions[0] != 0) {
            core = text.substring(0, positions[0]) + core;
        }
        if (positions[1] != text.length()) {
            core = core + text.substring(positions[1], text.length());
        }
        text = core;
    }

    public void removeHtmlTag(HtmlTag.Type type, int position){
        int htmlTagPos = this.getCharacterFormatting(type,position);

        if(htmlTagPos == -1){
            return;
        }

        HtmlTag tag = htmlTags.get(htmlTagPos);
        //Getting the text between the tags. The '2' comes from "<>", the '3' from "</>", and
        //the '1' because substring is exclusive to the end position, so we need to make sure
        //we grab the final letter.
        String core = text.substring(tag.getStartPosition() + 2 + tag.getTag().length(),
                tag.getEndPosition() - 3 - tag.getTag().length() + 1);

        if(tag.getStartPosition() != 0){
          core = text.substring(0,tag.getStartPosition()) + core;
        }
        if(tag.getEndPosition() != text.length()){
            core = core + text.substring(tag.getEndPosition() + 1, text.length());
        }
        text = core;
        htmlTags.remove(htmlTagPos);
    }

    public int getCharacterFormatting(HtmlTag.Type type, int position){
        for(int i = 0; i < htmlTags.size(); i++){
            HtmlTag tag = htmlTags.get(i);
            if(tag.getType() == type){
                if(position >= tag.getStartPosition()
                        && position <= tag.getEndPosition())
                {
                    return i;
                }
            }
        }
        return -1;
    }

    public int[] setPositionsToLine(int referencePos){
        setPositions(referencePos,'\n');
        return positions;
    }

    public int[] setPositionsToWord(int referencePos){
        setPositions(referencePos,' ');
        return positions;
    }

    public void setPositions(int referencePos, char boundary) {
        this.positions = new int[]{findStartingBoundary(referencePos,boundary),
            findEndBoundary(referencePos,boundary)};
    }

    private int findStartingBoundary(int referencePos, char boundary){
        int start = 0;
        boolean found = false;
        int i = referencePos - 1;
        while (!found && i > 0) {
            if (text.charAt(i) == boundary) {
                start = i;
                found = true;
            }
            i--;
        }

        return start;
    }

    private int findEndBoundary(int referencePos, char boundary){
        int end = text.length();
        boolean found = false;
        int i = referencePos + 1;
        while (!found && i < text.length()) {
            if (text.charAt(i) == boundary) {
                end = i;
                found = true;
            }
            i++;
        }

        return end;
    }
}
