package com.satchlapp.util;

import com.satchlapp.lists.Constants;
import com.satchlapp.model.Content;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sara on 2/21/2017.
 */
public class TextContentFormatter {

    private Map<Integer,Integer> activeFormats;

    public TextContentFormatter(){
        activeFormats = new HashMap<>();
    }


    /**
     * Returns the index of all the active formats that are affecting the content based on the
     * cursor position.
     * @param content the content object being checked for formatting
     * @param position the position in the content to be checked for formatting
     * @return the types of active formats affecting the position and their index in the Qualifier
     * array (stored in the Content)
     */
    public void findActiveFormattingPositions(Content content, int position){
        activeFormats.clear();

        for(int i = 0; i < content.getQualifiers().size(); i++){
            Content.Qualifier q = content.getQualifier(i);
            if(Integer.valueOf(q.getSpecification(0).getValue())<= position &&
                    position <= Integer.valueOf(q.getSpecification(1).getValue())){
                activeFormats.put(q.getType(), i);
            }
        }
    }

    /**
     * Returns the index of the corresponding format type from the activeFormats map.
     * @param formatType an int that corresponds to a format type.
     * @return the index of the format type
     * @see com.satchlapp.lists.Constants
     */
    public int getActiveFormatIndex(int formatType){
        return activeFormats.get(formatType);
    }

    /**
     * Removes the specified format type from the map of active formats
     * @param formatType an int that corresponds to a format type.
     * @see com.satchlapp.lists.Constants
     */
    public void removeActiveFormat(int formatType){
        activeFormats.remove(formatType);
    }

    /**
     * Returns true if the active formats map contains the specified format type.
     * @param formatType an int that corresponds to a format type.
     * @return true if map contains specified format type.
     * @see com.satchlapp.lists.Constants
     */
    public boolean isFormatActive(int formatType){
        return activeFormats.containsKey(formatType);
    }

    public void setFormatting(int formatType, int[] positions, Content content){
        Content.Qualifier qualifier = content.addNewQualifier();
        qualifier.setType(formatType);
        qualifier.addNewSpecification()
                .setType("int")
                .setName("starting_position")
                .setValue(String.valueOf(positions[0]));
        qualifier.addNewSpecification()
                .setType("int")
                .setName("ending_position")
                .setValue(String.valueOf(positions[1]));
    }

    public int[] getLinePositions(int referencePos, Content content){
        return getPositions(referencePos, '\n', content);
    }

    public int[] getWordPositions(int referencePos, Content content){
        return getPositions(referencePos,' ', content);
    }

    public int[] getPositions(int referencePos, char boundary, Content content) {
        if(content.getType() != Constants.CONTENT_TYPE_TEXT){
            return null;
        }

        String text = content.getValue();

        if(text == null){
            return new int[] {0,0};
        }

        return new int[]{findStartingBoundary(referencePos,boundary, text),
                findEndBoundary(referencePos,boundary, text)};
    }

    private int findStartingBoundary(int referencePos, char boundary, String text){
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

    private int findEndBoundary(int referencePos, char boundary, String text){
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
